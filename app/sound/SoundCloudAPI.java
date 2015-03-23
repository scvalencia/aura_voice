package sound;

/**
 * Created by scvalencia on 3/22/15.
 */


import java.io.File;

public class SoundCloudAPI {

    public static SoundCloudDriver soundcloud;
    private static String soundcloudId ="07fe9e7f76d4ac14db7bed65c2241a9d";
    private static String soundcloudSecret ="e66ea659eb26c0346df0a7670062237d";
    private static String soundcloudEmail ="aura201510@gmail.com";
    private static String soundcloudPsw ="aura123456789";

    static {
        initConnection();
    }

    public static void initConnection() {
        soundcloud = new SoundCloudDriver(soundcloudId,soundcloudSecret);
        soundcloud.login(soundcloudEmail, soundcloudPsw);
    }

    public static Long upload(String title, File content) {
        SoundCloudTrack track = new SoundCloudTrack(title, content);
        SoundCloudTrack newTrack = soundcloud.postTrack(track);
        return newTrack.getId() + 0L;
    }

}
