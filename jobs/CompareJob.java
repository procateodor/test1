package services.user.jobs;

import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import services.user.models.FileDTO;
import services.user.models.Submission;
import services.user.models.User;
import services.user.repositories.SubmissionRepository;
import services.user.repositories.UserRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CompareJob {

    private static final Logger logger = Logger.getLogger(CompareJob.class.getName());

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "*/30 * * * * *")
    public void CompareFiles() {
        Submission currentSubmission = null;

        try {
            int processingSubmissions = submissionRepository.countAllByStatus("processing");

            if (processingSubmissions > 0) {
                return;
            }

            Submission submission = submissionRepository.findFirstByStatus("pending");

            if (submission == null) {
                return;
            }

            currentSubmission = submission;

//            submission.setStatus("processing");
//            submissionRepository.save(submission);

            List<String> urls = Stream.of(currentSubmission.getUrls().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            if (urls.size() < 2) {
                submission.setStatus("error");
                submissionRepository.save(submission);
                return;
            }

            Optional<User> userOptional = userRepository.findById(submission.getUserId());
            User user = null;

            if (userOptional.isPresent()) {
                user = userOptional.get();
            }

            if (user == null) {
                submission.setStatus("error");
                submissionRepository.save(submission);
                return;
            }

            List<FileDTO> files = getFiles(urls, user);

            String basePath = "./compare/";

            FileUtils.deleteDirectory(new File(basePath));
            new File(basePath).mkdirs();

            for (FileDTO file : files) {
                String filePath = file.getPath().replaceAll("/", "-");
                String owner = filePath.split("-")[0];

                File ownerDir = new File(basePath + owner);

                if (!ownerDir.exists()) {
                    ownerDir.mkdir();
                }

                FileWriter myWriter = new FileWriter(basePath + owner + "/" + filePath);
                myWriter.write(file.getContent());
                myWriter.close();
            }

//            URL mossUrl = getMossUrl();
//            System.out.println(mossUrl.toString());
//            parseMossUrl(mossUrl);
        } catch (Exception e) {
            e.printStackTrace();
            if (currentSubmission != null) {
                currentSubmission.setStatus("pending");
                submissionRepository.save(currentSubmission);
            }
        }
    }

    private List<FileDTO> getFiles(List<String> urls, User user) {
        List<FileDTO> files = new ArrayList<>();

        for (String url : urls) {
            String[] pieces = url.split("/");

            String username = pieces[pieces.length - 2];
            String repoName = pieces[pieces.length - 1];

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder(
                        URI.create("https://api.github.com/repos/" + username + "/" + repoName + "/git/trees/main?recursive=true"))
                        .header("authorization", "token " + user.getToken())
                        .build();

                HttpResponse<String> gitResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                String body = gitResponse.body();

                JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                JSONObject repoData = (JSONObject) parser.parse(body);
                JSONArray repoTree = (JSONArray) parser.parse(repoData.getAsString("tree"));

                for (Object o : repoTree) {
                    JSONObject fileData = (JSONObject) o;
                    String filePath = fileData.getAsString("path");

                    if (filePath.endsWith(".js")) {
                        request = HttpRequest.newBuilder(
                                URI.create(fileData.getAsString("url")))
                                .header("authorization", "token " + user.getToken())
                                .header("accept", "application/vnd.github.VERSION.raw")
                                .build();

                        gitResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                        body = gitResponse.body();

                        FileDTO file = new FileDTO(String.join("/", new String[]{username, repoName, filePath}), body);

                        files.add(file);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.WARNING,"Encountered an invalid git url!");
            }
        }

        return files;
    }

    private URL getMossUrl() throws MossException, IOException {
        Collection<File> comparingFiles = FileUtils.listFiles(new File("./compare"), new String[] { "js" }, true);

        SocketClient socketClient = new SocketClient();
        socketClient.setUserID("520190020");

        socketClient.setLanguage("javascript");
        socketClient.run();

        for (File f : comparingFiles) {
            socketClient.uploadFile(f);
        }

        socketClient.sendQuery();

        return socketClient.getResultURL();
    }

    private void parseMossUrl(URL mossUrl) throws IOException {
        Document document = Jsoup.connect(mossUrl.toString()).get();
        Element table = document.select("table").get(0);
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); ++i) {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            String[] tokens1 = cols.get(0).text().split("/");
            String firstFileName = tokens1[tokens1.length - 2];

            int percent1 = Integer.parseInt(tokens1[tokens1.length - 1]
                            .replace("(", "").replace(")", "").replace("%", "").replace(" ", ""));

            String[] tokens2 = cols.get(1).text().split("/");
            String secondFileName = tokens2[tokens2.length - 2];

            int percent2 = Integer.parseInt(tokens2[tokens2.length - 1]
                    .replace("(", "").replace(")", "").replace("%", "").replace(" ", ""));

            int percent = Math.max(percent1, percent2);

            if (percent > 70) {

            }
        }
    }
}
