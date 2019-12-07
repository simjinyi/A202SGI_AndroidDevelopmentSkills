package com.example.pointofsales.view.product.manage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.state.ProductFormState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.ProductFormViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Abstract class for the product form fragment, since the same view can be shared between both edit and add product
 * This class will be used in registration for edit and add product feature
 */
public abstract class ProductFormFragment extends Fragment {

    // Constants as the key for the iamge selection operation
    private static final int SELECT_IMAGE = 1;
    private static final int IMAGE_HEIGHT_SCALE = 300;

    // ViewModel
    private ProductFormViewModel mProductFormViewModel;

    // View components
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
    protected ImageButton mBtnDelete;

    protected LoadingScreen mLoadingScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign the reference to the view components
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
        mBtnDelete = getView().findViewById(R.id.ibDelete);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModel from the ViewModelProviders
        mProductFormViewModel = ViewModelProviders.of(getActivity()).get(ProductFormViewModel.class);

        // ImageButton opens the file manager for the user to select the image to be added
        mIbEditProductImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
            }
        });

        // Cancels and goes back to the home fragment
        mBtnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        // Submit button calls the abstract "submit" method which will be implemented by the child classes
        // Product submission will be handled differently for adding and editing product
        mBtnSubmit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                submit();
            }
        });

        // Observe the ProductFormState for validation
        mProductFormViewModel.getProductFormState().observe(getViewLifecycleOwner(), new Observer<ProductFormState>() {
            @Override
            public void onChanged(ProductFormState productFormState) {
                if (productFormState == null)
                    return;

                mBtnSubmit.setEnabled(productFormState.isDataValid());

                // Prompt name error
                if (productFormState.getNameError() != null)
                    mEtProductName.setError(getString(productFormState.getNameError()));

                // Prompt price error
                if (productFormState.getPriceError() != null)
                    mEtProductPrice.setError(getString(productFormState.getPriceError()));

                // Prompt inventory error
                if (productFormState.getInventoryError() != null)
                    mEtProductInventoryQuantity.setError(getString(productFormState.getInventoryError()));

                // Prompt points error
                if (productFormState.getPointsError() != null)
                    mEtProductPoints.setError(getString(productFormState.getPointsError()));
            }
        });

        // Declare a textwatcher to call for validation when the form fields was changed by the user
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Calls the ViewModel for validation
                mProductFormViewModel.productFormDataChanged(mEtProductName.getText().toString(), mEtProductPrice.getText().toString(), mEtProductInventoryQuantity.getText().toString(), mEtProductPoints.getText().toString());
            }
        };

        // Add the textwatcher to all the editTexts in the form
        mEtProductName.addTextChangedListener(afterTextChangedListener);
        mEtProductPrice.addTextChangedListener(afterTextChangedListener);
        mEtProductInventoryQuantity.addTextChangedListener(afterTextChangedListener);
        mEtProductPoints.addTextChangedListener(afterTextChangedListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Ensure that the intent returns a valid result
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                try {

                    // Get the image with picasso and resize the image to the height of 100dp
                    Picasso.with(getActivity())
                            .load(data.getData())
                            .resize(0, IMAGE_HEIGHT_SCALE)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                    // Load the image into the view
                                    mBitmap = bitmap;
                                    mIvProductImage.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                    // Prompt an error if the image failed to be obtained
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_set_image), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                } catch (Exception e) {

                    // Prompt an error if the image failed to be obtained
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_set_image), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Get the product object from the form data entered by the user
     * @return a product object holding the form data
     */
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

    /**
     * Set the product data into the form
     * @param product product object to be used in setting the data
     */
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

    // Abstract method submit
    public abstract void submit();
}
