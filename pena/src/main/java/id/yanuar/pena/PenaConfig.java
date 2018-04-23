package id.yanuar.pena;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yanuar Arifin
 * halo@yanuar.id
 */
public class PenaConfig implements Parcelable {
    private String filenamePrefix;
    private String backgroundImage;
    private int backgroundColor;
    private String fileDirectory;
    private String toolbarTitle;
    private int orientation;
    private int fileFormat;

    public PenaConfig() {
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }

    public void setFilenamePrefix(String prefix) {
        this.filenamePrefix = prefix;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public String getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(int fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.filenamePrefix);
        parcel.writeString(this.backgroundImage);
        parcel.writeInt(this.backgroundColor);
        parcel.writeString(this.fileDirectory);
        parcel.writeString(this.toolbarTitle);
        parcel.writeInt(this.orientation);
        parcel.writeInt(this.fileFormat);

    }

    protected PenaConfig(Parcel in) {
        filenamePrefix = in.readString();
        backgroundImage = in.readString();
        backgroundColor = in.readInt();
        fileDirectory = in.readString();
        toolbarTitle = in.readString();
        orientation = in.readInt();
        fileFormat = in.readInt();
    }

    public static final Creator<PenaConfig> CREATOR = new Creator<PenaConfig>() {
        @Override
        public PenaConfig createFromParcel(Parcel in) {
            return new PenaConfig(in);
        }

        @Override
        public PenaConfig[] newArray(int size) {
            return new PenaConfig[size];
        }
    };
}
