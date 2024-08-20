package com.example.easy_sale;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private MutableLiveData<List<User>> users;
    private MutableLiveData<String> error ;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);
        this.users = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadUsers() {
        if (isLoading || !hasMorePages) {
            return;
        }

        isLoading = true;
        repository.fetchUsers(currentPage, new UserRepository.RepositoryCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                List<User> currentList = users.getValue();
                if (currentList == null) {
                    users.setValue(result);
                } else {
                    currentList.addAll(result);
                    users.setValue(currentList);
                }
                currentPage++;
                isLoading = false;
                hasMorePages = !result.isEmpty();
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading = false;
            }
        });
    }

    public void updateUser(User user) {
        repository.updateUser(user, new UserRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                List<User> currentList = users.getValue();
                if (currentList != null) {
                    int index = currentList.indexOf(result);
                    if (index != -1) {
                        currentList.set(index, result);
                        users.setValue(currentList);


                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
    public void cleanTask(){
        repository.cleanup();
    }
    public void deleteUser(User user) {
        repository.deleteUser(user, new UserRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                List<User> currentList = users.getValue();
                if (currentList != null) {
                    currentList.remove(result);
                    users.setValue(currentList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
}

