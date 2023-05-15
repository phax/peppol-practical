package com.helger.peppol.supplementary.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.timing.StopWatch;

public class MainCheckLogPerformance
{
  interface IRun
  {
    void run (int nIdx, String sParam);
  }

  static class V1 implements IRun
  {
    private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckLogPerformance.V1.class);

    public void run (final int nIdx, final String sParam)
    {
      LOGGER.info ("This is run " + nIdx + " with param " + sParam);
    }
  }

  static class V2 implements IRun
  {
    private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckLogPerformance.V1.class);

    @SuppressWarnings ("boxing")
    public void run (final int nIdx, final String sParam)
    {
      LOGGER.info ("This is run {} with param {}", nIdx, sParam);
    }
  }

  public static void main (final String [] args)
  {
    final int nWarmup = 1000;
    final int nRuns = 1_000_000;

    // Warm up
    final V1 v1 = new V1 ();
    for (int i = 0; i < nWarmup; ++i)
      v1.run (nWarmup, "bla");
    final V2 v2 = new V2 ();
    for (int i = 0; i < nWarmup; ++i)
      v2.run (nWarmup, "bla");

    // Measure
    final StopWatch aSW = StopWatch.createdStarted ();
    for (int i = 0; i < nRuns; ++i)
      v1.run (i, "TEst");
    aSW.stop ();

    final StopWatch aSW2 = StopWatch.createdStarted ();
    for (int i = 0; i < nRuns; ++i)
      v2.run (i, "TEst");
    aSW2.stop ();

    System.out.println ("v1: " + aSW.getMillis () + "ms; v2: " + aSW2.getMillis () + "ms");
  }
}
