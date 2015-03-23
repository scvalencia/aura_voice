package sound;

import com.google.gson.Gson;
import com.soundcloud.api.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by scvalencia on 3/22/15.
 */
public class SoundCloudDriver {

    public enum Type {
        USER,
        TRACK,
        PLAYLIST,
        COMMENT,
        GROUP
    }

    public enum Rest {
        GET,
        PUT,
        POST,
        DELETE
    }

    protected final String app_client_id;
    protected final String app_client_secret;
    protected Token token;
    protected ApiWrapper wrapper;
    protected JSONParser parser;
    protected Gson gson;

    public SoundCloudDriver(String _app_client_id, String _app_client_secret) {
        this.app_client_id = _app_client_id;
        this.app_client_secret = _app_client_secret;

        this.parser = new JSONParser();
        this.gson = new Gson();

        wrapper = new ApiWrapper(app_client_id, app_client_secret, null, null);
        wrapper.setToken(null);
        wrapper.setDefaultContentType("application/json");
    }

    public SoundCloudDriver(String _app_client_id, String _app_client_secret, String _login_name, String _login_password) {
        this(_app_client_id, _app_client_secret);
        this.login(_login_name, _login_password);
    }

    public boolean login(String _login_name, String _login_password) {
        try {
            this.token = wrapper.login(_login_name, _login_password, Token.SCOPE_NON_EXPIRING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public SoundCloudTrack postTrack(SoundCloudTrack track) {
        return this.post("tracks", track);
    }

    public <T> T post(String api, Object value) {
        return this.api(api, Rest.POST, value, null);
    }

    private <T> T api(String api, Rest rest, Object value, String[] filters) {
        api = this.filterApiString(api);
        Type type = this.defineApiType(api);
        api = this.appendGetArgs(api, filters);

        try {
            Request resource;
            HttpResponse response;
            String klass, content;

            klass = value.getClass().getName();
            klass = klass.substring((klass.lastIndexOf('.') + 1));

            SoundCloudTrack track = ((SoundCloudTrack) value);
            resource = Request.to(Endpoints.TRACKS)
                        .add(Params.Track.TITLE, track.getTitle())
                        .add(Params.Track.TAG_LIST, track.getTagList())
                        .withFile(Params.Track.ASSET_DATA, track.getContent());

            response = wrapper.post(resource);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                String json = (String) (Http.formatJSON(Http.getString(response))).trim();
                SoundCloudTrack newTrack = gson.fromJson(replaceJsonsBlank(json), SoundCloudTrack.class);
                newTrack.setSoundCloud(this);
                return (T) newTrack;

            } else {
                System.err.println("Invalid status received: " + response.getStatusLine());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Type defineApiType(String api) {
        Type type = null;
        if (
                api.matches("^/me.json") ||
                        api.matches("^/me/(followings(/[0-9]+)?|followers(/[0-9]+)?).json") ||
                        api.matches("^/SoundCloudUsers(/[0-9]+)?.json") ||
                        api.matches("^/SoundCloudUsers/([0-9]+)/(followings|followers).json") ||
                        api.matches("^/groups/([0-9]+)/(SoundCloudUsers|moderators|members|contributors).json")
                ) {
            type = Type.USER;
        } else if (
                api.matches("^/tracks(/[0-9]+)?.json") ||
                        api.matches("^/me/(tracks|favorites)(/[0-9]+)?.json") ||
                        api.matches("^/SoundCloudUsers/([0-9]+)/(tracks|favorites).json")
                ) {
            type = Type.TRACK;
        } else if (
                api.matches("^/playlists(/[0-9]+)?.json") ||
                        api.matches("^/me/playlists.json") ||
                        api.matches("^/SoundCloudUsers/([0-9]+)/playlists.json") ||
                        api.matches("^/groups/([0-9]+)/tracks.json")
                ) {
            type = Type.PLAYLIST;
        } else if (
                api.matches("^/comments/([0-9]+).json") ||
                        api.matches("^/me/comments.json") ||
                        api.matches("^/tracks/([0-9]+)/comments.json")
                ) {
            type = Type.COMMENT;
        } else if (
                api.matches("^/groups(/[0-9]+)?.json") ||
                        api.matches("^/me/groups.json") ||
                        api.matches("^/SoundCloudUsers/([0-9]+)/groups.json")
                ) {
            type = Type.GROUP;
        }
        if (type == null) {
            // TODO: throw exception if type is invalid
        }
        return type;
    }

    private String appendGetArgs(String api, String[] args) {
        if (args != null) {
            if (args.length > 0 && args.length % 2 == 0) {
                api += "?";
                for (int i = 0, l = args.length; i < l; i += 2) {
                    if (i != 0) {
                        api += "&";
                    }
                    api += (args[i] + "=" + args[i + 1]);
                }
                if (this.token == null) {
                    api += ("&consumer_key=" + this.app_client_id);
                }
            }
        } else {
            api += "?consumer_key=" + this.app_client_id;
        }
        return api;
    }

    private String filterApiString(String api) {
        if (api.length() > 0) {
            // Suppressing / Forcing the `/.` at the beginning
            if (!api.startsWith("/")) {
                api = "/" + api;
            }
            // Removing the '/' at the end
            if (api.charAt(api.length() - 1) == '/') {
                api = api.substring(0, api.length() - 1);
            }
            // Define the format to ".json"
            api = api.replace(".format", ".json").replace(".xml", ".json");
            if (api.indexOf(".json") == -1) {
                api += ".json";
            }
            return api;
        }
        // TODO: throw exception if api string is empty
        return null;
    }

    private String replaceJsonsBlank(String string) {
        Pattern pattern = Pattern.compile(":(?: +)?\"\"");
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll(":null");
    }

}
