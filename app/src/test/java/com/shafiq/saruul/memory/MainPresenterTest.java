package com.shafiq.saruul.memory;

import com.shafiq.saruul.memory.handlers.PermissionHandler;
import com.shafiq.saruul.memory.handlers.StorageHandler;
import com.shafiq.saruul.memory.main.MainContract;
import com.shafiq.saruul.memory.main.MainPresenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    private static final int NUMBER_OF_MEMORY_CARDS = 12;

    @Mock
    private MainContract.View view;
    @Mock
    private Random random;
    @Mock
    private List<String> filePaths;
    @Mock
    private List<Integer> unusedMemoryIndexes;
    @Mock
    private PermissionHandler permissionHandler;
    @Mock
    private StorageHandler storageHandler;

    @Test
    public void testNewGamePermissionReadExternalStorageNotGrantedShouldShowRequestPermissionRationale() {
        when(permissionHandler.permissionReadExternalStorageGranted()).thenReturn(false);
        when(permissionHandler.shouldShowRequestPermissionRationale()).thenReturn(true);

        MainContract.Presenter presenter = new MainPresenter(
                random, filePaths, unusedMemoryIndexes, permissionHandler, storageHandler);
        presenter.takeView(view);
        presenter.newGame(NUMBER_OF_MEMORY_CARDS);

        verify(permissionHandler, times(0)).requestReadExternalStoragePermission();
        verify(view, times(1)).showReadExternalStoragePermissionMissingError();
    }

    @Test
    public void testNewGamePermissionReadExternalStorageNotGrantedShouldNotShowRequestPermissionRationale() {
        when(permissionHandler.permissionReadExternalStorageGranted()).thenReturn(false);
        when(permissionHandler.shouldShowRequestPermissionRationale()).thenReturn(false);

        MainContract.Presenter presenter = new MainPresenter(
                random, filePaths, unusedMemoryIndexes, permissionHandler, storageHandler);
        presenter.takeView(view);
        presenter.newGame(NUMBER_OF_MEMORY_CARDS);

        verify(permissionHandler, times(1)).requestReadExternalStoragePermission();
        verify(view, times(0)).showReadExternalStoragePermissionMissingError();
    }

    @Test
    public void testNewGamePermissionReadExternalStorageGrantedFilePathsEmpty() {
        when(permissionHandler.permissionReadExternalStorageGranted()).thenReturn(true);
        when(filePaths.isEmpty()).thenReturn(true);

        MainContract.Presenter presenter = new MainPresenter(
                random, filePaths, unusedMemoryIndexes, permissionHandler, storageHandler);
        presenter.takeView(view);
        presenter.newGame(NUMBER_OF_MEMORY_CARDS);

        verify(view, times(1)).showProgressBar();
        verify(view, times(1)).showNotEnoughImagesError();
        verify(view, times(0)).showGameBoard();
        verify(storageHandler, times(1)).getAllImagePaths();
    }

    @Test
    public void testNewGamePermissionReadExternalStorageGrantedFilePathsSmallerThenNumberOfMemoryCards() {
        when(permissionHandler.permissionReadExternalStorageGranted()).thenReturn(true);
        when(filePaths.isEmpty()).thenReturn(false);
        when(filePaths.size()).thenReturn(NUMBER_OF_MEMORY_CARDS - 1);

        MainContract.Presenter presenter = new MainPresenter(
                random, filePaths, unusedMemoryIndexes, permissionHandler, storageHandler);
        presenter.takeView(view);
        presenter.newGame(NUMBER_OF_MEMORY_CARDS);

        verify(view, times(1)).showProgressBar();
        verify(view, times(1)).showNotEnoughImagesError();
        verify(view, times(0)).showGameBoard();
        verify(storageHandler, times(1)).getAllImagePaths();
    }

    @Test
    public void testNewGamePermissionReadExternalStorageGrantedFilePathsSameAsNumberOfMemoryCards() {
        final MainContract.Presenter presenter = new MainPresenter(
                random, filePaths, unusedMemoryIndexes, permissionHandler, storageHandler);
        presenter.takeView(view);

        when(permissionHandler.permissionReadExternalStorageGranted()).thenReturn(true);
        when(filePaths.isEmpty()).thenReturn(false);
        when(filePaths.size()).thenReturn(NUMBER_OF_MEMORY_CARDS);
        when(unusedMemoryIndexes.size()).thenReturn(NUMBER_OF_MEMORY_CARDS);
        when(unusedMemoryIndexes.get(anyInt())).thenReturn(0);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                presenter.onImageLoaded(true, 0);
                return null;
            }
        }).when(view).loadImage(anyInt(), anyString());

        presenter.newGame(NUMBER_OF_MEMORY_CARDS);

        verify(view, times(1)).showProgressBar();
        verify(view, times(NUMBER_OF_MEMORY_CARDS)).loadImage(anyInt(), anyString());
        verify(view, times(1)).showGameBoard();
        verify(storageHandler, times(0)).getAllImagePaths();
    }

    @Test
    public void testNewGamePermissionReadExternalStorageGrantedFilePathsBiggerThenNumberOfMemoryCards() {
        final MainContract.Presenter presenter = new MainPresenter(
                random, filePaths, unusedMemoryIndexes, permissionHandler, storageHandler);
        presenter.takeView(view);

        when(permissionHandler.permissionReadExternalStorageGranted()).thenReturn(true);
        when(filePaths.isEmpty()).thenReturn(false);
        when(filePaths.size()).thenReturn(NUMBER_OF_MEMORY_CARDS + 1);
        when(unusedMemoryIndexes.size()).thenReturn(NUMBER_OF_MEMORY_CARDS);
        when(unusedMemoryIndexes.get(anyInt())).thenReturn(0);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                presenter.onImageLoaded(true, 0);
                return null;
            }
        }).when(view).loadImage(anyInt(), anyString());

        presenter.newGame(NUMBER_OF_MEMORY_CARDS);

        verify(view, times(1)).showProgressBar();
        verify(view, times(NUMBER_OF_MEMORY_CARDS)).loadImage(anyInt(), anyString());
        verify(view, times(1)).showGameBoard();
        verify(storageHandler, times(0)).getAllImagePaths();
    }
}
