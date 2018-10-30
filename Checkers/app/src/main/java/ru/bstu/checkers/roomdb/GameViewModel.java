package ru.bstu.checkers.roomdb;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI.
 * You can also use a ViewModel to share data between fragments. The ViewModel is part of the lifecycle library.
 * A ViewModel holds your app's UI data in a lifecycle-conscious way that survives configuration changes.
 * Separating your app's UI data from your Activity and Fragment classes lets you better follow the single
 * responsibility principle: Your activities and fragments are responsible for drawing data to the screen,
 * while your ViewModel can take care of holding and processing all the data needed for the UI.
 * In the ViewModel, use LiveData for changeable data that the UI will use or display.
 * Using LiveData has several benefits:
 *     You can put an observer on the data (instead of polling for changes) and only update the
 *     the UI when the data actually changes.
 *     The Repository and the UI are completely separated by the ViewModel.
 *There are no database calls from the ViewModel, making the code more testable.
 */
public class GameViewModel extends AndroidViewModel {
    public GameViewModel (Application application) {
        super(application);
    }
    /*//Add a private member variable to hold a reference to the repository.
    private MyRepository mRepository;
    //Add a private LiveData member variable to cache the list of games.
    private LiveData<List<Game>> mAllGames;
    //Add a constructor that gets a reference to the repository and gets the list of games from the repository.
    public GameViewModel (Application application) {
        super(application);
        mRepository = new MyRepository(application);
        mAllGames = mRepository.getAllGames();
    }
    //Add a "getter" method for all the games. This completely hides the implementation from the UI.
    public LiveData<List<Game>> getAllGames() { return mAllGames; }
    //Create a wrapper insert() method that calls the Repository's insert() method.
    //In this way, the implementation of insert() is completely hidden from the UI.
    public void insert(Game game, ArrayList<Item>[] ways) {
        mRepository.insert(game, ways);
    }
    public void delete(String gameName) { mRepository.delete(gameName); }
    public void load(String gameName) { mRepository.load(gameName); }*/
}
