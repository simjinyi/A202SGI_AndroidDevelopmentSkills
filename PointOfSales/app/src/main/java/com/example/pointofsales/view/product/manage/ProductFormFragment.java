package com.example.pointofsales.view.product.manage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pointofsales.R;
import com.example.pointofsales.helper.LoadingScreenHelper;
import com.example.pointofsales.model.Product;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public abstract class ProductFormFragment extends Fragment {

    private static final int SELECT_IMAGE = 1;
    private static final int IMAGE_HEIGHT_SCALE = 300;

    private Bitmap mBitmap;
    private ImageView mIvProductImage;
    private ImageButton mIbEditProductImage;
    private EditText mEtProductName;
    private EditText mEtProductPrice;
    private EditText mEtProductInventoryQuantity;
    private EditText mEtProductPoints;
    private Button mBtnCancel;
    private Button mBtnSubmit;
    private Switch mSwEnabled;
    private ProgressBar mPbLoading;

    protected LoadingScreenHelper mLoadingScreenHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBitmap = null;
        mIvProductImage = getView().findViewById(R.id.ivProductImage);
        mIbEditProductImage = getView().findViewById(R.id.ibEditProductImage);
        mEtProductName = getView().findViewById(R.id.etProductName);
        mEtProductPrice = getView().findViewById(R.id.etProductPrice);
        mEtProductInventoryQuantity = getView().findViewById(R.id.etProductInventoryQuantity);
        mEtProductPoints = getView().findViewById(R.id.etProductPoints);
        mBtnCancel = getView().findViewById(R.id.btnCancel);
        mBtnSubmit = getView().findViewById(R.id.btnSubmit);
        mSwEnabled = getView().findViewById(R.id.swEnabled);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreenHelper = new LoadingScreenHelper(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIbEditProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ConfirmationDialogHelper.getConfirmationDialog(getActivity(),
//                        getResources().getString(R.string.quit_manage_confirmation),
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                Navigation.findNavController(getView()).navigateUp();
//                                getActivity().onBackPressed();
//                            }
//                        }).show();

                getActivity().onBackPressed();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    submit();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                try {

                    Picasso.with(getActivity())
                            .load(data.getData())
                            .resize(0, IMAGE_HEIGHT_SCALE)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    mBitmap = bitmap;
                                    mIvProductImage.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_set_image), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                } catch (Exception e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_set_image), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validate() {
        return true;
    }

    public Product getProductObject() {

        Product product = new Product();

        product.setImage(mBitmap);
        product.setName(mEtProductName.getText().toString());
        product.setPrice(Float.parseFloat(mEtProductPrice.getText().toString()));
        product.setInventoryQuantity(Integer.parseInt(mEtProductInventoryQuantity.getText().toString()));
        product.setPointPerItem(Integer.parseInt(mEtProductPoints.getText().toString()));
        product.setDisabled(!mSwEnabled.isChecked());
        product.setTotalSales(0);

        return product;
    }

    public void setData(Product product) {

        if (product.getImage() != null) {
            mBitmap = product.getImage();
            mIvProductImage.setImageBitmap(mBitmap);
        }

        mEtProductName.setText(product.getName());
        mEtProductPrice.setText(String.format("%.2f", product.getPrice()));
        mEtProductInventoryQuantity.setText(String.valueOf(product.getInventoryQuantity()));
        mEtProductPoints.setText(String.valueOf(product.getPointPerItem()));
        mSwEnabled.setEnabled(!product.isDisabled());
    }

    public abstract void submit();
}