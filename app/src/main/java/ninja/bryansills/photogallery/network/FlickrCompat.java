package ninja.bryansills.photogallery.network;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

public class FlickrCompat {
    public static PhotoList getList(
            @NotNull InterestingnessInterface interestingnessInterface,
            @Nullable Date date,
            @Nullable Set<String> extras,
            int perPage,
            int page
    ) throws FlickrException, JSONException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        return interestingnessInterface.getList(date, extras, perPage, page);
    }
}
