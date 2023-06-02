public class VSIDS implements Comparable<VSIDS>{
    public String literal;
    public Integer nonNegativeNum;
    public Integer negativeNum;

    public VSIDS(String lit, int nonNegativeNum, int negativeNum){
        this.literal = lit;
        this.nonNegativeNum = nonNegativeNum;
        this.negativeNum = negativeNum;
    }

    @Override
    public int compareTo(VSIDS other){
        int comparisonResult =  Integer.compare(this.nonNegativeNum + this.nonNegativeNum, other.nonNegativeNum + other.nonNegativeNum);

        if (comparisonResult == 0){
            if(this.nonNegativeNum < this.negativeNum){
                if(other.nonNegativeNum < other.negativeNum){
                    comparisonResult = Integer.compare(this.negativeNum, other.negativeNum);
                }else{
                    comparisonResult = Integer.compare(this.negativeNum, other.nonNegativeNum);
                }
            }else{
                if(other.nonNegativeNum < other.negativeNum){
                    comparisonResult = Integer.compare(this.nonNegativeNum, other.negativeNum);
                }else{
                    comparisonResult = Integer.compare(this.nonNegativeNum, other.nonNegativeNum);
                }
            }
        }

        if(comparisonResult > 0){
            return -1;
        }else if(comparisonResult < 0){
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof VSIDS)){
            return false;
        }

        VSIDS other = (VSIDS) obj;

        return (this.literal.equals(other.literal) && this.nonNegativeNum.equals(other.nonNegativeNum) && this.negativeNum.equals(other.negativeNum));
    }
}
