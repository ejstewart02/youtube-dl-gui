import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubedlExecutor extends SwingWorker<Boolean, Integer> {
    private String url;
    private File directory;

    YoutubedlExecutor(File directory, String url) {
        this.directory = directory;
        this.url = url;
    }

    @Override
    protected Boolean doInBackground() {
        boolean done = false;

        File ytdlpath = new File("youtube-dl/yt-dlp.exe");

        ProcessBuilder pB = new ProcessBuilder(ytdlpath.getAbsolutePath(), url);
        pB.directory(directory);

        Pattern percentagePattern = Pattern.compile("\\[download\\]\\s+(\\d+\\.\\d+)(?=%)");

        try {
            Process process = pB.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    Matcher matcher = percentagePattern.matcher(line);
                    if (matcher.find()) {
                        double progressDouble = Double.parseDouble(matcher.group(1));
                        int progress = (int) Math.round(progressDouble);

                        System.out.println(progress);
                        setProgress(progress);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return done;
    }
}
