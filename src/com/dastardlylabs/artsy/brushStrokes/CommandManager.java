package com.dastardlylabs.artsy.brushStrokes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.os.Handler;

import com.dastardlylabs.interfaces.ICommandItem;

public class CommandManager {
	protected List<ICommandItem> currentStack;
	protected List<ICommandItem> redoStack;

	public  CommandManager(){
		currentStack = Collections.synchronizedList(new ArrayList<ICommandItem>());
		redoStack = Collections.synchronizedList(new ArrayList<ICommandItem>());
	}

	public void addCommand(ICommandItem command){
		redoStack.clear();
		currentStack.add(command);
	}

	public void undo (){
		final int length = currentStackLength();
		if ( length > 0) {
			final ICommandItem undoCommand = currentStack.get(  length - 1  );
			currentStack.remove( length - 1 );
			undoCommand.undo();
			redoStack.add( undoCommand );
		}
	}

	public int currentStackLength(){
		return currentStack.toArray().length;
	}

	public void executeAll( Canvas canvas, Handler completeHandler){
		if( currentStack != null ){
			synchronized( currentStack ) {
				final Iterator<ICommandItem> i = currentStack.iterator();
				while ( i.hasNext() ){
					final ICommandItem command = (ICommandItem) i.next();
					command.setContext(canvas);
					command.redo();
                    completeHandler.sendEmptyMessage(1);
				}
			}
		}
	}

	public boolean hasMoreRedo(){
		return  redoStack.toArray().length > 0;
	}

	public boolean hasMoreUndo(){
		return  currentStack.toArray().length > 0;
	}

	public void redo(){
		final int length = redoStack.toArray().length;
		if ( length > 0) {
			final ICommandItem redoCommand = redoStack.get(  length - 1  );
			redoStack.remove( length - 1 );
			currentStack.add( redoCommand );
		}
	}
}