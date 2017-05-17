package com.learningmachine.android.app.ui.issuer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentAddIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.StringUtils;

import javax.inject.Inject;

public class AddIssuerFragment extends LMFragment {

    private static final String ARG_ISSUER_URL = "AddIssuerFragment.IssuerUrl";
    private static final String ARG_ISSUER_NONCE = "AddIssuerFragment.IssuerNonce";

    @Inject protected BitcoinManager mBitcoinManager;
    @Inject protected IssuerManager mIssuerManager;

    private FragmentAddIssuerBinding mBinding;

    public static AddIssuerFragment newInstance(String issuerUrlString, String nonce) {
        Bundle args = new Bundle();
        args.putString(ARG_ISSUER_URL, issuerUrlString);
        args.putString(ARG_ISSUER_NONCE, nonce);

        AddIssuerFragment fragment = new AddIssuerFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_issuer, container, false);

        handleArgs();

        mBinding.addIssuerNonceEditText.setOnEditorActionListener(mActionListener);

        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_issuer, menu);
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        String issuerUrlString = args.getString(ARG_ISSUER_URL);
        if (!StringUtils.isEmpty(issuerUrlString)) {
            mBinding.addIssuerUrlEditText.setText(issuerUrlString);
        }

        String issuerNonce = args.getString(ARG_ISSUER_NONCE);
        if (!StringUtils.isEmpty(issuerNonce)) {
            mBinding.addIssuerNonceEditText.setText(issuerNonce);
        }
    }

    private void startIssuerIntroduction() {
        hideKeyboard();
        String introUrl = mBinding.addIssuerUrlEditText.getText()
                .toString();
        String nonce = mBinding.addIssuerNonceEditText.getText()
                .toString();

        mBitcoinManager.getBitcoinAddress()
                .doOnSubscribe(() -> displayProgressDialog(R.string.fragment_add_issuer_adding_issuer_progress_dialog_message))
                .flatMap(bitcoinAddress -> mIssuerManager.addIssuer(introUrl, bitcoinAddress, nonce))
                .compose(bindToMainThread())
                .subscribe(aVoid -> {
                    hideProgressDialog();
                    getActivity().finish();
                }, throwable -> {
                    displayErrors(throwable, R.string.error_title_message);
                });
    }

    private TextView.OnEditorActionListener mActionListener = (v, actionId, event) -> {
        if (actionId == getResources().getInteger(R.integer.action_done) || actionId == EditorInfo.IME_ACTION_DONE) {
            startIssuerIntroduction();
            return false;
        }
        return false;
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_issuer_verify:
                startIssuerIntroduction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
