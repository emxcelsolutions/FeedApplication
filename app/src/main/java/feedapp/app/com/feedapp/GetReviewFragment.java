package feedapp.app.com.feedapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetReviewFragment extends Fragment {

    /*ToDo: Variable Declaration */
    private Button mButton_carnumberreview, mButton_drivernamereview;

    public GetReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_review, container, false);

        MainActivity.mImageView.setVisibility(View.VISIBLE);

        //TODO car number review fragment called here
        mButton_carnumberreview = (Button) view.findViewById(R.id.mButton_carnumberreview);
        mButton_carnumberreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarReviewFragment mCarReviewFragment = new CarReviewFragment();
                FragmentManager mFragmentManager = getFragmentManager();
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.content_main, mCarReviewFragment);
                MainActivity.current_fragment="Car Review";
                mFragmentTransaction.commit();
            }
        });

        //TODO driver name review fragment called here
        mButton_drivernamereview = (Button) view.findViewById(R.id.mButton_drivernamereview);
        mButton_drivernamereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverReviewFragment mDriverReviewFragment = new DriverReviewFragment();
                FragmentManager mFragmentManager = getFragmentManager();
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.content_main, mDriverReviewFragment);
                MainActivity.current_fragment="Driver Review";
                mFragmentTransaction.commit();
            }
        });

        return view;
    }

 
}
