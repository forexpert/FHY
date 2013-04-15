package com.mengruojun.farm;

import algorithm.simulatedannealing.magiccube.MagicCube;
import algorithm.simulatedannealing.magiccube.MagicCube.*;
import org.apache.log4j.Logger;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;

/**
 * MagicCubeTest
 */
public class MagicCubeTest {

  Logger logger = Logger.getLogger(this.getClass());
  @Test
  public void testMagicCubeOperation(){
    MagicCubeStatus mcs1 = MagicCubeStatus.getOriginal();
    MagicCubeStatus mcs = MagicCubeStatus.getOriginal();

    mcs.doOperation(OperationType.U);

    assertNotSame(mcs, mcs1);

    mcs.doOperation(OperationType.u);

    assertEquals(mcs, mcs1);


    mcs.doOperation(OperationType.R);
    mcs.doOperation(OperationType.r);
    assertEquals(mcs, mcs1);

    mcs.doOperation(OperationType.L);
    mcs.doOperation(OperationType.l);
    assertEquals(mcs, mcs1);

    mcs.doOperation(OperationType.B);
    mcs.doOperation(OperationType.b);
    assertEquals(mcs, mcs1);

    mcs.doOperation(OperationType.D);
    mcs.doOperation(OperationType.d);
    assertEquals(mcs, mcs1);


    mcs.doOperation(OperationType.F);
    mcs.doOperation(OperationType.f);
    assertEquals(mcs, mcs1);

  }

  /**
   new MagicCubeStatus(
   new Color[][]{{},{},{}},
   new Color[][]{{},{},{}},
   new Color[][]{{},{},{}},
   new Color[][]{{},{},{}},
   new Color[][]{{},{},{}},
   new Color[][]{{},{},{}}
   )
   */

  @Test
  public void testMagicCubeOperation3(){
    MagicCubeStatus mcs = MagicCubeStatus.getOriginal();

    mcs.doOperation(OperationType.F);//F
    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Blue,Color.Blue,Color.Blue},{Color.Blue,Color.Blue,Color.Blue},{Color.White,Color.White,Color.White}},
            new Color[][]{{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red}},
            new Color[][]{{Color.Blue,Color.Yellow,Color.Yellow},{Color.Blue,Color.Yellow,Color.Yellow},{Color.Blue,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.White,Color.White,Color.Green},{Color.White,Color.White,Color.Green},{Color.White,Color.White,Color.Green}},
            new Color[][]{{Color.Orange, Color.Orange, Color.Orange},{Color.Orange, Color.Orange, Color.Orange},{Color.Orange, Color.Orange, Color.Orange}},
            new Color[][]{{Color.Yellow,Color.Yellow,Color.Yellow},{Color.Green,Color.Green,Color.Green},{Color.Green,Color.Green,Color.Green}}
    ),mcs);

    mcs.doOperation(OperationType.u);//U-
    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.Blue,Color.Blue,Color.White},{Color.Blue,Color.Blue,Color.White}},
            new Color[][]{{Color.White,Color.White,Color.Green},{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red}},
            new Color[][]{{Color.Red,Color.Red,Color.Red},{Color.Blue,Color.Yellow,Color.Yellow},{Color.Blue,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Orange,Color.Orange,Color.Orange},{Color.White,Color.White,Color.Green},{Color.White,Color.White,Color.Green}},
            new Color[][]{{Color.Blue,Color.Yellow,Color.Yellow},{Color.Orange,Color.Orange,Color.Orange},{Color.Orange,Color.Orange,Color.Orange}},
            new Color[][]{{Color.Yellow,Color.Yellow,Color.Yellow},{Color.Green,Color.Green,Color.Green},{Color.Green,Color.Green,Color.Green}}
    ),mcs);

    mcs.doOperation(OperationType.F);//F2
    mcs.doOperation(OperationType.F);
    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.Blue,Color.Blue,Color.White},{Color.Yellow,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red},{Color.Green,Color.White,Color.White}},
            new Color[][]{{Color.Green,Color.Red,Color.Red},{Color.Green,Color.Yellow,Color.Yellow},{Color.Orange,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Orange,Color.Orange,Color.Blue},{Color.White,Color.White,Color.Blue},{Color.White,Color.White,Color.Red}},
            new Color[][]{{Color.Blue,Color.Yellow,Color.Yellow},{Color.Orange,Color.Orange,Color.Orange},{Color.Orange,Color.Orange,Color.Orange}},
            new Color[][]{{Color.White,Color.Blue,Color.Blue},{Color.Green,Color.Green,Color.Green},{Color.Green,Color.Green,Color.Green}}
    ),mcs);

    mcs.doOperation(OperationType.d); //D-

    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.Blue,Color.Blue,Color.White},{Color.Yellow,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Red,Color.Red,Color.Red},{Color.Red, Color.Red,Color.Red},{Color.Orange,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Green,Color.Red,Color.Red},{Color.Green,Color.Yellow,Color.Yellow},{Color.Orange,Color.Orange,Color.Orange}},
            new Color[][]{{Color.Orange,Color.Orange,Color.Blue},{Color.White,Color.White,Color.Blue},{Color.Green,Color.White,Color.White}},
            new Color[][]{{Color.Blue,Color.Yellow,Color.Yellow},{Color.Orange,Color.Orange,Color.Orange},{Color.White,Color.White,Color.Red}},
            new Color[][]{{Color.Blue,Color.Green,Color.Green},{Color.Blue,Color.Green,Color.Green},{Color.White,Color.Green,Color.Green}}
    ),mcs);

    mcs.doOperation(OperationType.B); //B
    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Red,Color.Yellow,Color.Orange},{Color.Blue,Color.Blue,Color.White},{Color.Yellow,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red},{Color.Orange,Color.Yellow,Color.Yellow}},
            new Color[][]{{Color.Green,Color.Red,Color.Green},{Color.Green,Color.Yellow,Color.Green},{Color.Orange,Color.Orange,Color.White}},
            new Color[][]{{Color.White,Color.Orange,Color.Blue},{Color.Blue,Color.White,Color.Blue},{Color.Blue,Color.White,Color.White}},
            new Color[][]{{Color.White,Color.Orange, Color.Blue},{Color.White, Color.Orange, Color.Yellow},{Color.Red,Color.Orange, Color.Yellow}},
            new Color[][]{{Color.Blue,Color.Green,Color.Green},{Color.Blue, Color.Green, Color.Green},{Color.Orange, Color.White, Color.Green}}
    ),mcs);




    mcs.doOperation(OperationType.U); //U
    mcs.doOperation(OperationType.r); //R-
    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Yellow,Color.Blue,Color.Red},{Color.Yellow, Color.Blue, Color.White},{Color.Yellow, Color.White, Color.White}},
            new Color[][]{{Color.Green, Color.Red, Color.Red},{Color.Red, Color.Red, Color.Yellow},{Color.Orange, Color.Yellow, Color.Orange}},
            new Color[][]{{Color.Blue, Color.Green, Color.White},{Color.Orange, Color.Yellow, Color.Orange},{Color.White, Color.Green, Color.Orange}},
            new Color[][]{{Color.Red, Color.Red, Color.Red, },{Color.Blue, Color.White, Color.Blue},{Color.Blue, Color.White, Color.White}},
            new Color[][]{{Color.Green,Color.Orange, Color.Blue},{Color.Green,Color.Orange,Color.Yellow},{Color.Green, Color.Orange, Color.Yellow}},
            new Color[][]{{Color.Blue,Color.Green,Color.Green},{Color.Blue, Color.Green, Color.Red, },{Color.Orange, Color.White, Color.Yellow}}
    ),mcs);


    mcs.doOperation(OperationType.f); //F-
    assertEquals(new MagicCubeStatus(
            new Color[][]{{Color.Yellow, Color.Blue, Color.Red},{Color.Yellow, Color.Blue, Color.White},{Color.Blue, Color.Orange, Color.White}},
            new Color[][]{{Color.Red, Color.Yellow, Color.Orange},{Color.Red,Color.Red,Color.Yellow},{Color.Green, Color.Red, Color.Orange}},
            new Color[][]{{Color.Green, Color.Green, Color.White},{Color.Green, Color.Yellow, Color.Orange},{Color.Blue,Color.Green,Color.Orange}},
            new Color[][]{{Color.Red,Color.Red,Color.White},{Color.Blue,Color.White,Color.White},{Color.Blue,Color.White,Color.Yellow}},
            new Color[][]{{Color.Green,Color.Orange,Color.Blue},{Color.Green,Color.Orange,Color.Yellow},{Color.Green, Color.Orange, Color.Yellow}},
            new Color[][]{{Color.Red, Color.Blue, Color.White},{Color.Blue,Color.Green,Color.Red},{Color.Orange,Color.White,Color.Yellow}}
    ),mcs);

    mcs.doOperation(OperationType.L); //L



    mcs.doOperation(OperationType.d); //D-

    //Color[][] upper, Color[][] front, Color[][] right, Color[][] left, Color[][] back, Color[][] down
    /**
     new MagicCubeStatus(
     new Color[][]{{},{},{}},
     new Color[][]{{},{},{}},
     new Color[][]{{},{},{}},
     new Color[][]{{},{},{}},
     new Color[][]{{},{},{}},
     new Color[][]{{},{},{}}
     )
     */


  }

  @Test
  public void testMagicCubeOperation2(){
    //public MagicCubeStatus(Color[][] upper, Color[][] front, Color[][] right, Color[][] left, Color[][] back, Color[][] down ){
    MagicCubeStatus mcs_expected = new MagicCubeStatus(
            new Color[][]{{Color.Red,Color.Orange,Color.White},{Color.White,Color.Blue,Color.Yellow},{Color.Yellow,Color.Red,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.White,Color.Red,Color.Yellow},{Color.Green,Color.Green,Color.White}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Red},{Color.Orange,Color.Yellow,Color.Red},{Color.Green,Color.Green,Color.Red}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Orange},{Color.Red,Color.White,Color.Orange},{Color.Green,Color.Green,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Yellow},{Color.Yellow,Color.Orange,Color.White},{Color.Green,Color.Green,Color.Yellow}},
            new Color[][]{{Color.Yellow,Color.Red,Color.Orange},{Color.White, Color.Green,Color.Yellow},{Color.Red,Color.Orange,Color.White}});
    MagicCubeStatus mcs = MagicCubeStatus.getOriginal();

    mcs.doOperation(OperationType.F);//F

    mcs.doOperation(OperationType.u);//U-
    mcs.doOperation(OperationType.F);//F2
    mcs.doOperation(OperationType.F);
    mcs.doOperation(OperationType.d); //D-

    mcs.doOperation(OperationType.B); //B
    mcs.doOperation(OperationType.U); //U
    mcs.doOperation(OperationType.r); //R-
    mcs.doOperation(OperationType.f); //F-
    mcs.doOperation(OperationType.L); //L

    mcs.doOperation(OperationType.d); //D-
    mcs.doOperation(OperationType.r); //R-
    mcs.doOperation(OperationType.u); //U-
    mcs.doOperation(OperationType.L); //L
    mcs.doOperation(OperationType.U); //U

    mcs.doOperation(OperationType.b); //B-
    mcs.doOperation(OperationType.D); //D2
    mcs.doOperation(OperationType.D);
    mcs.doOperation(OperationType.r); //R-
    mcs.doOperation(OperationType.F);//F

    mcs.doOperation(OperationType.U); //U2
    mcs.doOperation(OperationType.U);
    mcs.doOperation(OperationType.D); //D2
    mcs.doOperation(OperationType.D);

    assertEquals(mcs, mcs_expected);
  }

  @Test
  public void testClone(){
    MagicCubeStatus mcs_1 = new MagicCubeStatus(
            new Color[][]{{Color.Red,Color.Orange,Color.White},{Color.White,Color.Blue,Color.Yellow},{Color.Yellow,Color.Red,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.White,Color.Red,Color.Yellow},{Color.Green,Color.Green,Color.White}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Red},{Color.Orange,Color.Yellow,Color.Red},{Color.Green,Color.Green,Color.Red}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Orange},{Color.Red,Color.White,Color.Orange},{Color.Green,Color.Green,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Yellow},{Color.Yellow,Color.Orange,Color.White},{Color.Green,Color.Green,Color.Yellow}},
            new Color[][]{{Color.Yellow,Color.Red,Color.Orange},{Color.White, Color.Green,Color.Yellow},{Color.Red,Color.Orange,Color.White}});

    MagicCubeStatus mcs_2 = mcs_1.cloneOne();

    assertEquals(mcs_1, mcs_2);
    assertNotSame(mcs_1,mcs_2);
    mcs_1.doOperation(OperationType.U);
    mcs_2.doOperation(OperationType.f);
    assertFalse(mcs_1.equals(mcs_2));
    logger.info(mcs_1.getMixedValue());
    logger.info(mcs_2.getMixedValue());

  }

  @Test
  public void testMixedValue(){
    MagicCubeStatus mcs_1 = new MagicCubeStatus(
            new Color[][]{{Color.Red,Color.Orange,Color.White},{Color.White,Color.Blue,Color.Yellow},{Color.Yellow,Color.Red,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.White,Color.Red,Color.Yellow},{Color.Green,Color.Green,Color.White}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Red},{Color.Orange,Color.Yellow,Color.Red},{Color.Green,Color.Green,Color.Red}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Orange},{Color.Red,Color.White,Color.Orange},{Color.Green,Color.Green,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Yellow},{Color.Yellow,Color.Orange,Color.White},{Color.Green,Color.Green,Color.Yellow}},
            new Color[][]{{Color.Yellow,Color.Red,Color.Orange},{Color.White, Color.Green,Color.Yellow},{Color.Red,Color.Orange,Color.White}});

    MagicCubeStatus mcs_2 = MagicCubeStatus.getOriginal();
    logger.info(mcs_1.getMixedValue());
    logger.info(mcs_2.getMixedValue());
  }
}
