package edu.mcs.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author nareddisr
 * Is backed by the original list and has accessor methods to retrieve items of the list in batches  
 *
 * @param <T>
 */

//TODO: this should be moved to rtsh

public class BatchedList<T>
{
	public static final int ORACLE_IN_CLAUSE_SIZE = 1000;
	private int batchSize = 100;
	private List<T> backend;
	private Iterator<T> iterator;
	private int currentBatchNum = 0;
	
	/**
	 * @param backend
	 * @param batchSize	
	 */
	public BatchedList(List<T> backend, int batchSize)
	{
		this.batchSize = batchSize;
		this.backend = backend;
		this.iterator = backend.iterator();
	}
	
	/**
	 * @return
	 */
	public boolean hasNextBatch()
	{
		if(iterator.hasNext())
			return true;
		return false;
	}
	
	public List<T> nextBatch()
	{
		List<T> batch = new ArrayList<T>();
		if(hasNextBatch())
		{
			currentBatchNum++;
			
			for(int i=0; i < batchSize && iterator.hasNext(); i++)
			{
				batch.add(iterator.next());
			}
		}
		return batch;
	}
	
	/**
	 * Returns the current batch number, starting with 1
	 */
	public int getCurrentBatchNum(){
		return currentBatchNum;
	}
	
	/**
	 * Returns the total number of batches, calculated
	 * using the list size and the batch size
	 */
	public int getTotalBatches(){
		return backend.size()/batchSize+1;
	}
	
	/**
	 * Returns the starting index for the current batch
	 */
	public int getStartIndexInBatch(){
		return (currentBatchNum - 1) * batchSize;
	}
	
	/**
	 * Returns the ending index for the current batch
	 */
	public int getEndIndexInBatch(){
		int endBatch = getStartIndexInBatch() + batchSize - 1;
		if (endBatch+1 > backend.size()) 
			endBatch = backend.size() - 1;
		
		return endBatch;
	}
	
}
