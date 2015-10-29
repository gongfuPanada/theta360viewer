package com.eje_c.theta360viewer;

import android.content.Intent;

import com.eje_c.meganekko.AndroidResource;
import com.eje_c.meganekko.Material;
import com.eje_c.meganekko.MeganekkoActivity;
import com.eje_c.meganekko.SceneObject;
import com.eje_c.meganekko.Texture;
import com.eje_c.meganekko.VrContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends MeganekkoActivity {

    private SceneObject player;

    @Override
    protected void oneTimeInit(final VrContext context) {
        parseAndSetScene(R.xml.scene);
        player = findObjectByName("player");

        Intent intent = getIntent();
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

                return;
            } catch (IOException e) {
                e.printStackTrace();
                createVrToastOnUiThread(e.getLocalizedMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        finish();
    }

    private void loadUrl(String url) {

        String filename = url.hashCode() + ".equirectangular";
        File photo = new File(getCacheDir(), filename);

        // Download
        if (!photo.exists()) {
            try {
                FileUtils.copyURLToFile(new URL(url), photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (photo.exists()) {
            try {
                getVrContext().loadTexture(textureCallback, new AndroidResource(photo));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }
    }

    private final AndroidResource.TextureCallback textureCallback = new AndroidResource.TextureCallback() {
        @Override
        public boolean stillWanted(AndroidResource androidResource) {
            return false;
        }

        @Override
        public void loaded(Texture texture, AndroidResource androidResource) {
            Material material = new Material(getVrContext());
            material.setMainTexture(texture);
            player.getRenderData().setMaterial(material);
        }

        @Override
        public void failed(Throwable throwable, AndroidResource androidResource) {
            createVrToastOnUiThread(throwable.getLocalizedMessage());
        }
    };

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
