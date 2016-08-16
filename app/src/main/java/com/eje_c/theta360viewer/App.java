package com.eje_c.theta360viewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.utility.Threads;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class App extends MeganekkoApp {

    private SceneObject player;

    protected App(Meganekko meganekko) {
        super(meganekko);
    }

    @Override
    public void init() {
        super.init();
        setSceneFromXML(R.xml.scene);
        player = getScene().findObjectById(R.id.player);

        Intent intent = ((Activity) getContext()).getIntent();
        String url = intent.getDataString();

        if (url != null) {

            HttpsURLConnection conn = null;

            try {
                URL source = new URL(url);
                conn = (HttpsURLConnection) source.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2540.0 Safari/537.36");
                conn.connect();
                InputStream stream = conn.getInputStream();
                String html = IOUtils.toString(stream);

                Document document = Jsoup.parse(html);
                String photoUrl = document.select("#urlText").val();

                loadUrl(photoUrl);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }
    }

    private void loadUrl(String url) {

        String filename = url.hashCode() + ".equirectangular";
        final File photo = new File(getContext().getCacheDir(), filename);

        // Download
        if (!photo.exists()) {
            try {
                FileUtils.copyURLToFile(new URL(url), photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (photo.exists()) {
            Threads.spawn(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bmp = BitmapFactory.decodeFile(photo.getAbsolutePath());
                    runOnGlThread(new Runnable() {
                        @Override
                        public void run() {
                            player.material(Material.from(bmp));
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, int repeatCount) {
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, int repeatCount) {
        return true;
    }

    @Override
    public boolean onKeyShortPress(int keyCode, int repeatCount) {
        return true;
    }
}
