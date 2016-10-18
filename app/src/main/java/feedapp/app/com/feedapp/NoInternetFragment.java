package feedapp.app.com.feedapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternetFragment extends FragmentActivity {

    /*ToDo: This class used for when internet not detect*/
    public NoInternetFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_no_internet);

    }
}
