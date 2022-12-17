import java.util.Arrays;

public class Vector {
    private final double[] doubElements;

    public Vector(double[] _elements) { doubElements = _elements; }

    /**
     * Extracts the element at the given index from the doubElements array. If the given index is greater than the
     * length of the array or a negative number, then the value returned is -1. Otherwise, the element is returned from
     * doubElements
     * @param _index array index
     * @return the number at the given index
     */
    public double getElementatIndex(int _index) {
        return _index > doubElements.length - 1 || _index < 0 ? -1 : doubElements[_index];
    }

    /**
     * Sets the element at the given index in the doubElements array. If the given index is greater than the length of
     * the array or a negative number, then the element at the final index of the array is set to the input.
     * @param _value element to add/change in the array
     * @param _index array index
     */
    public void setElementatIndex(double _value, int _index) {
        if (_index > getVectorSize() - 1 || _index < 0) doubElements[getVectorSize() - 1] = _value;
        else doubElements[_index] = _value;
    }

    public double[] getAllElements() { return doubElements; }

    public int getVectorSize() { return doubElements.length; }

    /**
     * Resizes the current vector to the given size. The process depends on _size's relationship to the current vector
     * 1. If _size is equal or _size is a zero or negative number return the current vector 2. If _size is smaller,
     * reduce the size of the current vector retaining the element up to _size 3. If _size is larger, expand the size of
     * the current vector with -1's after _size
     * @param _size new array size
     * @return the re-sized array
     */
    public Vector reSize(int _size) {
        if (_size == getVectorSize() || _size <= 0) return this; //case 1

        //deals with the case 2 by default
        double[] result = Arrays.copyOf(getAllElements(), _size); //TODO TIP: This required some research

        //case 3
        if (_size > getVectorSize())
            for (int i = getVectorSize(); i < _size; i++) result[i] = -1; //iterate starting from getVectorSize()

        return new Vector(result);
    }

    /**
     * Adds the input vector to the current vector. In the case that one vector is smaller than the other, calls
     * reSize() to make the smaller vector bigger.
     * @param _v added vector
     * @return the vector result of the addiction
     */
    public Vector add(Vector _v) {
        Vector current = this; //track the current vector
        Vector other = _v; //tracks the input vector

        //expend the smaller vector and store the expended version in the relevant variable
        if (_v.getVectorSize() > getVectorSize()) current = reSize(_v.getVectorSize());
        else if (_v.getVectorSize() < getVectorSize()) other = _v.reSize(getVectorSize());

        double[] result = new double[current.getVectorSize()]; //new array for the added elements

        //iterate and add the two numbers, saving the result in the new array
        for (int i = 0; i < current.getVectorSize(); i++)
            result[i] = current.getElementatIndex(i) + other.getElementatIndex(i);

        return new Vector(result);
    }

    /**
     * Subtracts the input vector from the current vector. In the case that one vector is smaller than the other, calls
     * reSize() to make the smaller vector bigger
     * @param _v vector to subtract by
     * @return the vector result of the subtraction
     */
    public Vector subtraction(Vector _v) {
        Vector current = this; //track the current vector
        Vector other = _v; //tracks the input vector

        //expend the smaller vector and store the expended version in the relevant variable
        if (_v.getVectorSize() > getVectorSize()) current = reSize(_v.getVectorSize());
        else if (_v.getVectorSize() < getVectorSize()) other = _v.reSize(getVectorSize());

        double[] result = new double[current.getVectorSize()]; //new array for the subtracted elements

        //iterate and subtract the two numbers, saving the result in the new array
        for (int i = 0; i < current.getVectorSize(); i++)
            result[i] = current.getElementatIndex(i) - other.getElementatIndex(i);

        return new Vector(result);
    }

    /**
     * Helper method that multiplies the elements of two vectors of equal length together
     * @param _v vector to multiply by
     * @return the vector result of multiplication
     */
    private double[] multiply(Vector _v) {
        double[] result = new double[getVectorSize()]; //new array for the multiplied elements

        //iterate and multiply tne two numbers, saving the result in the new array
        for (int i = 0; i < getVectorSize(); i++) result[i] = getElementatIndex(i) * _v.getElementatIndex(i);

        return result;
    }

    /**
     * Calculates the dot product of two give vectors. In the case that one vector is smaller than the other, calls
     * reSize() to make the smaller vector bigger
     * @param _v vector to do dot product with
     * @return the vector result of the dot product
     */
    public double dotProduct(Vector _v) {
        Vector current = this; //track the current vector
        Vector other = _v; //tracks the input vector

        //expend the smaller vector and store the expended version in the relevant variable
        if (_v.getVectorSize() > getVectorSize()) current = reSize(_v.getVectorSize());
        else if (_v.getVectorSize() < getVectorSize()) other = _v.reSize(getVectorSize());

        //multiply the relevant elements together using the multiply() method then use streams to sum easily.
        return Arrays.stream(current.multiply(other)).sum();
    }

    /**
     * Helper method that returns the magnitude ||v|| of the current vector. It's the square root of the elements squared
     * and added together
     * @return magnitude of the current vector
     */
    private double magnitude() { return Math.sqrt(Arrays.stream(getAllElements()).map(x -> Math.pow(x, 2)).sum()); }

    /**
     * Calculates the cosine similarity between the current vector and the input vector. In the case that one vector is
     * smaller than the other, calls reSize() to make the smaller vector bigger. The formula is the dot product divided
     * by the magnitudes of each vector multiplied
     * @param _v vector to do cosine similarity with
     * @return the cosine similarity
     */
    public double cosineSimilarity(Vector _v) {
        Vector current = this; //track the current vector
        Vector other = _v; //tracks the input vector

        //expend the smaller vector and store the expended version in the relevant variable
        if (_v.getVectorSize() > getVectorSize()) current = reSize(_v.getVectorSize());
        else if (_v.getVectorSize() < getVectorSize()) other = _v.reSize(getVectorSize());

        return current.dotProduct(other) / (current.magnitude() * other.magnitude()); //using the formula
    }

    @Override
    public boolean equals(Object _obj) {
        Vector other = (Vector) _obj;

        //Arrays.equals() accounts for all that is mentioned in the brief
        return Arrays.equals(getAllElements(), other.getAllElements());
    }

    @Override
    public String toString() {
        StringBuilder mySB = new StringBuilder();
        for (int i = 0; i < this.getVectorSize(); i++) mySB.append(String.format("%.5f", doubElements[i])).append(",");

        mySB.delete(mySB.length() - 1, mySB.length());
        return mySB.toString();
    }
}
