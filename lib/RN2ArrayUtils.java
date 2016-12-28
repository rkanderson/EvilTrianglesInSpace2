package lib;

import java.lang.reflect.Array;

class RN2ArrayUtils {
	/**
	 * Class to contain some universal array utility functions.
	 */
	
	/**
	 * Concatenates 2 arrays.
	 * @param a first array
	 * @param b second array
	 * @return concatenated array
	 */
	public static <T> T[] concatenate (T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
	
	public static int[] concatenate (int[] a, int[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    int[] c = (int[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}

	public static boolean arraysEqual(int[] arr1, int[] arr2) {
		if(arr1.length != arr2.length) {
			return false;
		}
		for(int i=0; i<arr1.length; i++) {
			if(arr1[i] != arr2[i]) return false;
		}
		return true;
	}
	
	
}
