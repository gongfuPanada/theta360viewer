package com.eje_c.theta360viewer;

import com.eje_c.meganekko.Meganekko;
import com.eje_c.meganekko.MeganekkoApp;
import com.eje_c.meganekko.gearvr.MeganekkoActivity;

public class MainActivity extends MeganekkoActivity {

    @Override
    public MeganekkoApp createMeganekkoApp(Meganekko meganekko) {
        return new App(meganekko);
    }
}
