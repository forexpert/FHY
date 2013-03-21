package algorithm.simulatedannealing.magiccube;

import org.apache.log4j.Logger;



/**
 * A simulated annealing algorithm for Magic Cue
 * 结果：未能求解
 * 参考 http://cnbeta.com/articles/184963.htm
 *
 */
public class MagicCube{
  static Logger logger = Logger.getLogger(MagicCube.class);

  static void simulatedannealing(MagicCubeStatus targetStatus){
    /**
     *
     s := s0; e := E(s)                           // 設定目前狀態為s0，其能量E(s0)
     k := 0                                       // 評估次數k
     while k < kmax and e > emax                  // 若還有時間(評估次數k還不到kmax)且結果還不夠好(能量e不夠低)則：
     sn := neighbour(s)                         //   隨機選取一鄰近狀態sn
     en := E(sn)                                //   sn的能量為 E(sn)
     if random() < P(e, en, temp(k/kmax)) then  //   決定是否移至鄰近狀態sn
     s := sn; e := en                         //     移至鄰近狀態sn
     k := k + 1                                 //   評估完成，次數k加一
     return s                                     // 回傳狀態s
     *
     *
     */

    MagicCubeStatus currentStatus = targetStatus.cloneOne();
    int maxK = 10000;
    int k =0;
    Double emax = 0.0;
    while(k<maxK && currentStatus.getMixedValue() > emax){
      k++;
      MagicCubeStatus newStatus = currentStatus.cloneOne();
      OperationType ot = OperationType.GetRandomOperationType();
      newStatus.doOperation(ot);
      Double eNew = newStatus.getMixedValue();
      if(eNew < currentStatus.getMixedValue()|| Math.random() < 0.005 ){
        currentStatus = newStatus;
        logger.info("Do Operation: " +ot+ "; The E is " + currentStatus.getMixedValue());
      }
    }

    if(currentStatus.getMixedValue() == emax){
      logger.info("Done");
    } else {
      logger.info("Not-Done");
    }

  }

  public static void main(String[] args){
    MagicCubeStatus targetStatus = new MagicCubeStatus(
            new Color[][]{{Color.Red,Color.Orange,Color.White},{Color.White,Color.Blue,Color.Yellow},{Color.Yellow,Color.Red,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.White},{Color.White,Color.Red,Color.Yellow},{Color.Green,Color.Green,Color.White}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Red},{Color.Orange,Color.Yellow,Color.Red},{Color.Green,Color.Green,Color.Red}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Orange},{Color.Red,Color.White,Color.Orange},{Color.Green,Color.Green,Color.Orange}},
            new Color[][]{{Color.Blue,Color.Blue,Color.Yellow},{Color.Yellow,Color.Orange,Color.White},{Color.Green,Color.Green,Color.Yellow}},
            new Color[][]{{Color.Yellow,Color.Red,Color.Orange},{Color.White, Color.Green,Color.Yellow},{Color.Red,Color.Orange,Color.White}});

    MagicCube.simulatedannealing(targetStatus);
  }

  /**
   * status for a magic cube
   *        x
   *
   *        |
   *        |
   *       0|
   *      |   |
   *    |       |
   *   y         z
   *
   *
   *
   *
   *
   *
   */
  public static class MagicCubeStatus  implements Cloneable{
    Logger logger = Logger.getLogger(this.getClass());

    private Color[][] upper = new Color[3][3];
    private Color[][] front = new Color[3][3];
    private Color[][] right = new Color[3][3];
    private Color[][] left = new Color[3][3];
    private Color[][] back = new Color[3][3];
    private Color[][] down = new Color[3][3];



    public MagicCubeStatus cloneOne(){
      try {
        return (MagicCubeStatus)this.clone();
      } catch (CloneNotSupportedException e) {
        logger.error("", e);
      }
      return null;
    }

    @Override
    public boolean equals(Object o){
      if(! (o instanceof MagicCubeStatus)) return false;
      else {
        MagicCubeStatus oo = (MagicCubeStatus)o ;
        return compare(this.upper, oo.upper) && compare(this.front, oo.front) && compare(this.right, oo.right) &&
                compare(this.left, oo.left) && compare(this.back, oo.back) && compare(this.down, oo.down);
      }
    }

    @Override
    public String toString(){
      return "\nupper:" + sideToString(upper) +
              "\nfront:" + sideToString(front) +
              "\nright:" + sideToString(right) +
              "\nleft:" + sideToString(left) +
              "\nback:" + sideToString(back) +
              "\ndown:" + sideToString(down)
              ;
    }

    private String sideToString(Color[][] side) {
      StringBuffer re = new StringBuffer();
      for(int i =0; i<side.length; i++ ){
        for(int j =0; j<side[i].length; j++ ){
          re.append(side[i][j]);
          if(!(i==2&&j==2))re.append(",");
        }
      }
      return re.toString();
    }


    private boolean compare(Color[][] side1, Color[][]side2){
      for(int i =0; i<side1.length; i++ ){
        for(int j =0; j<side1[i].length; j++ ){
          if(side1[i][j] != side2[i][j] ) return false;
        }

      }
      return true;
    }


    public static MagicCubeStatus getOriginal(){
      Color[][] upper = new Color[][]{{Color.Blue,Color.Blue,Color.Blue},{Color.Blue,Color.Blue,Color.Blue},{Color.Blue,Color.Blue,Color.Blue}};
      Color[][] front = new Color[][]{{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red},{Color.Red,Color.Red,Color.Red}};
      Color[][] right = new Color[][]{{Color.Yellow,Color.Yellow,Color.Yellow},{Color.Yellow,Color.Yellow,Color.Yellow},{Color.Yellow,Color.Yellow,Color.Yellow}};
      Color[][] left = new Color[][]{{Color.White,Color.White,Color.White},{Color.White,Color.White,Color.White},{Color.White,Color.White,Color.White}};
      Color[][] back = new Color[][]{{Color.Orange,Color.Orange,Color.Orange},{Color.Orange,Color.Orange,Color.Orange},{Color.Orange,Color.Orange,Color.Orange}};
      Color[][] down = new Color[][]{{Color.Green,Color.Green,Color.Green},{Color.Green,Color.Green,Color.Green},{Color.Green,Color.Green,Color.Green}};
      return new MagicCubeStatus(upper, front, right, left, back, down);
    }
    
    public MagicCubeStatus(Color[][] upper, Color[][] front, Color[][] right, Color[][] left, Color[][] back, Color[][] down ){
      this.upper = upper;
      this.front = front;
      this.right = right;
      this.left = left;
      this.back = back;
      this.down = down;
    }

    /**
     * If all side are with the same colors, return 0; else return a pre-defined mixed value (todo).
     *
     * @return  Double
     */
    public Double getMixedValue(){
      return getMixedValueOfSide(upper) + getMixedValueOfSide(front) + getMixedValueOfSide(right)
              + getMixedValueOfSide(left) + getMixedValueOfSide(back) + getMixedValueOfSide(down);
    }

    /**
     * a mixedValue of a side
     * @param side side
     * @return Double
     */
    private Double getMixedValueOfSide(Color[][] side){
      Double re = 0.0;
      Color main = side[1][1];
      for(int i =0; i<side.length; i++ ){
        for(int j =0; j<side[i].length; j++ ){
          if(side[i][j]!= main){
            re += 1.0;
          }
        }
      }


      //if there are same colors in one line, but which is different from the main color, we can reduce the mixedValue by a certain value(say,2.1).
      if((side[0][0] == side[0][1] && side[0][1] == side[0][2] && side[0][2]!=main) ||
              (side[0][0] == side[1][0] && side[1][0] == side[2][0]&& side[2][0]!=main) ||
              (side[2][0] == side[2][1] && side[2][1] == side[2][2]&& side[2][2]!=main) ||
              (side[0][2] == side[1][2] && side[1][2] == side[2][2]&& side[2][2]!=main)
              ){
        re -= 2.1;
      }

      return re;

    }

    /**
     * Clockwise an input side and return the result
     * @param input Color[][]
     * @return   Color[][]
     */
    public Color[][] clockwise(Color[][] input){
      Color[][] re = new Color[3][3];
      re[0][0]= input[2][0];
      re[0][1]= input[1][0];
      re[0][2]= input[0][0];

      re[1][0]= input[2][1];
      re[1][1]= input[1][1];
      re[1][2]= input[0][1];

      re[2][0]= input[2][2];
      re[2][1]= input[1][2];
      re[2][2]= input[0][2];

      return re;
    }

    /**
     * antiClockwise an input side and return the result
     * @param input Color[][]
     * @return   Color[][]
     */
    public Color[][] antiClockwise(Color[][] input){
      Color[][] re = new Color[3][3];
      re[0][0]= input[0][2];
      re[0][1]= input[1][2];
      re[0][2]= input[2][2];

      re[1][0]= input[0][1];
      re[1][1]= input[1][1];
      re[1][2]= input[2][1];

      re[2][0]= input[0][0];
      re[2][1]= input[1][0];
      re[2][2]= input[2][0];

      return re;

    }

    public void doOperation(OperationType operationType){
      for(SmallOperationType sot: operationType.smallOperationTypes){
        doSmallOperation(sot);
      }
    }

    private void doSmallOperation(SmallOperationType smallOperationType){
      Color[] temp = null;
      Color[][] temp1 = null;
      switch (smallOperationType){
        case U:
          temp = new Color[]{front[0][0],front[0][1],front[0][2]};
          front[0][0] = right[0][0];
          front[0][1] = right[0][1];
          front[0][2] = right[0][2];

          right[0][0] = back[0][0];
          right[0][1] = back[0][1];
          right[0][2] = back[0][2];

          back[0][0] = left[0][0];
          back[0][1] = left[0][1];
          back[0][2] = left[0][2];

          left[0][0] = temp[0];
          left[0][1] = temp[1];
          left[0][2] = temp[2];

          upper = clockwise(upper);

          break;

        case F:
          temp = new Color[]{upper[2][0],upper[2][1],upper[2][2]};
          upper[2][0] = left[2][2];
          upper[2][1] = left[1][2];
          upper[2][2] = left[0][2];

          left[2][2] = down[0][2];
          left[1][2] = down[0][1];
          left[0][2] = down[0][0];

          down[0][0] = right[2][0];
          down[0][1] = right[1][0];
          down[0][2] = right[0][0];

          right[0][0] = temp[0];
          right[1][0] = temp[1];
          right[2][0] = temp[2];

          front = clockwise(front);

          break;

        case R:
          temp = new Color[]{upper[2][2],upper[1][2],upper[0][2]};
          upper[2][2] = front[2][2];
          upper[1][2] = front[1][2];
          upper[0][2] = front[0][2];

          front[2][2] = down[2][2];
          front[1][2] = down[1][2];
          front[0][2] = down[0][2];

          down[2][2] = back[0][0];
          down[1][2] = back[1][0];
          down[0][2] = back[2][0];

          back[0][0] = temp[0];
          back[1][0] = temp[1];
          back[2][0] = temp[2];

          right = clockwise(right);

          break;


        case L:
          temp = new Color[]{upper[0][0],upper[1][0],upper[2][0]};
          upper[0][0] = back[2][2];
          upper[1][0] = back[1][2];
          upper[2][0] = back[0][2];

          back[2][2] = down[0][0];
          back[1][2] = down[1][0];
          back[0][2] = down[2][0];

          down[0][0] = front[0][0];
          down[1][0] = front[1][0];
          down[2][0] = front[2][0];

          front[0][0] = temp[0];
          front[1][0] = temp[1];
          front[2][0] = temp[2];

          left = clockwise(left);

          break;

        case D:

          temp = new Color[]{front[2][0],front[2][1],front[2][2]};
          front[2][0] = left[2][0];
          front[2][1] = left[2][1];
          front[2][2] = left[2][2];

          left[2][0] = back[2][0];
          left[2][1] = back[2][1];
          left[2][2] = back[2][2];

          back[2][0] = right[2][0];
          back[2][1] = right[2][1];
          back[2][2] = right[2][2];

          right[2][0] = temp[0];
          right[2][1] = temp[1];
          right[2][2] = temp[2];

          down = clockwise(down);
          break;
        case B:
          temp = new Color[]{upper[0][0],upper[0][1],upper[0][2]};
          upper[0][0] = right[0][2];
          upper[0][1] = right[1][2];
          upper[0][2] = right[2][2];

          right[0][2] = down[2][2];
          right[1][2] = down[2][1];
          right[2][2] = down[2][0];

          down[2][2] = left[2][0];
          down[2][1] = left[1][0];
          down[2][0] = left[0][0];

          left[2][0] = temp[0];
          left[1][0] = temp[1];
          left[0][0] = temp[2];

          back = clockwise(back);

          break;
      }
    }
  }

  /**
   * U means 顺时针旋转Upper side
   */
  public enum SmallOperationType{
    U,F,R,L,B,D
  }

  /**
   * u means 逆时针旋转Upper side; U means 顺时针旋转Upper side
   */
  public enum OperationType{


    u(SmallOperationType.U, SmallOperationType.U, SmallOperationType.U), U(SmallOperationType.U),
    f(SmallOperationType.F, SmallOperationType.F, SmallOperationType.F), F(SmallOperationType.F),
    r(SmallOperationType.R, SmallOperationType.R, SmallOperationType.R), R(SmallOperationType.R),

    l(SmallOperationType.L, SmallOperationType.L, SmallOperationType.L), L(SmallOperationType.L),
    b(SmallOperationType.B, SmallOperationType.B, SmallOperationType.B), B(SmallOperationType.B),
    d(SmallOperationType.D, SmallOperationType.D, SmallOperationType.D), D(SmallOperationType.D) ;

    SmallOperationType[] smallOperationTypes;

    OperationType(SmallOperationType ...smallOperationTypes){
      this.smallOperationTypes = smallOperationTypes;
    }

    public static OperationType GetRandomOperationType(){
      double random = Math.random();
      if(0<=random && random< 1/12.0) return u;
      if(1/12.0<=random && random< 2/12.0) return U;
      if(2/12.0<=random && random< 3/12.0) return f;
      if(3/12.0<=random && random< 4/12.0) return F;
      if(4/12.0<=random && random< 5/12.0) return r;
      if(5/12.0<=random && random< 6/12.0) return R;
      if(6/12.0<=random && random< 7/12.0) return l;
      if(7/12.0<=random && random< 8/12.0) return L;
      if(8/12.0<=random && random< 9/12.0) return b;
      if(9/12.0<=random && random< 10/12.0) return B;
      if(10/12.0<=random && random< 11/12.0) return d;
      if(11/12.0<=random && random<= 12/12.0) return D;

      return null;
    }
  }

  public enum  Color{
    Red,
    Yellow,
    Green,
    White,
    Orange,
    Blue
  }

}
