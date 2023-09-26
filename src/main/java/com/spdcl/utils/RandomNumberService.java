package com.spdcl.utils;

import org.springframework.stereotype.Service;

@Service("randomNumberService")
public class RandomNumberService {

	public static int getRandomNumber(int maxLimit)
    {
            return (int) (Math.random() * maxLimit);
    }
	public static int getNDigitRandomNumber(int n)
    {
            int upperLimit = getMaxNDigitNumber(n);
            int lowerLimit = getMinNDigitNumber(n);
            int s = getRandomNumber(upperLimit);
            if (s < lowerLimit)
            {
                    s += lowerLimit;
            }
            return s;
    }
    /**
     * This method returns a random number between lowerLimit and upperLimit.
     */
    public static int getRandomNumber(int lowerLimit, int upperLimit)
    {
            int v = (int) (Math.random() * upperLimit);
            if (v < lowerLimit)
            {
                    v += lowerLimit;
            }
            return v;
    }

    /**
     * This method returns a largest n digit number. The value returned depends on integer limit.
     */
    private static int getMaxNDigitNumber(int n)
    {
            int s = 0;
            int j = 10;
            for (int i = 0; i < n; i++)
            {
                    int m = 9;
                    s = (s * j) + m;
            }
            return s;
    }

    /**
     * This method returns a lowest n digit number. The value returned depends on integer limit.
     */
    private static int getMinNDigitNumber(int n)
    {
            int s = 0;
            int j = 10;
            for (int i = 0; i < n - 1; i++)
            {
                    int m = 9;
                    s = (s * j) + m;
            }
            return s + 1;
    }
}
