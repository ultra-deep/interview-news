package com.seven.di;


import com.seven.util.Repository;
import com.seven.util.RepositoryImp;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module @InstallIn(ViewModelComponent.class) public class ViewModelScopeModule {
    @Provides public Repository authRepository(){
        return new RepositoryImp();
    }
}
