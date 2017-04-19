package com.learningmachine.android.app.data.inject;

import android.content.Context;
import android.provider.ContactsContract;

import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.LMDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    BitcoinManager providesBitcoinManager(Context context) {
        return new BitcoinManager(context);
    }

    @Provides
    @Singleton
    LMDatabase provideLmDatabase(Context context) {
        return new LMDatabase(context);
    }

    @Provides
    @Singleton
    ImageStore providesImageStore(Context context) {
        return new ImageStore(context);
    }

    @Provides
    @Singleton
    IssuerStore providesIssuerStore(LMDatabase database, ImageStore imageStore) {
        return new IssuerStore(database, imageStore);
    }

}
