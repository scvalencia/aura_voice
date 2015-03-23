package sound;

import java.io.File;

/**
 * Created by scvalencia on 3/22/15.
 */
public class SoundCloudTrack {

    private File content;
    private String title;
    private String tagList;
    private Integer id;

    private transient SoundCloudDriver sc;

    public SoundCloudTrack()
    {
        this.setTagList("");
    }

    public SoundCloudTrack(String title, File file) {
        this.setTitle(title);
        this.setContent(file);
        this.setTagList(title);
    }

    public File getContent() {
        return content;
    }

    public void setContent(File content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTagList() {
        return tagList;
    }

    public void setTagList(String tagList) {
        this.tagList = tagList;
    }

    public Integer getId() {
        return id;
    }

    public void setSoundCloud(SoundCloudDriver _sc)
    {
        this.sc = _sc;
    }
}
