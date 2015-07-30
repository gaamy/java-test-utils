/*
 * this code based in Jon Skeet's answer: http://stackoverflow.com/questions/2902458/is-it-possible-to-pass-arithmetic-operators-to-a-method-in-java
 * */

package trace;
/*
 * On évalue la la fitness de la partie du else du coté du if, etvise versa
 * 
 * Exemple d'utilisation
 * if(a>b)
 * 		GREATEROREQUAL
 * else
 * 		LESS
 * 
 * */
public enum BranchDistanceOperator
{
	EQUAL("==") {
	        @Override public double evaluate(double x1, double x2) {
	            return Math.abs(x1 - x2) + K;
	        }
	},
	
	NOTEQUAL("!=") {
	        @Override public double evaluate(double x1, double x2) {
	            return K;
	        }
	},
	GREATER("<") {
        @Override public double evaluate(double x1, double x2) {
            return Math.abs(x1-x2)+K;
        }
    },
    LESS(">") {
        @Override public double evaluate(double x1, double x2) {
            return Math.abs(x2-x1)+K;
        }
    },    
    LESSOREQUAL("<=") {
        @Override public double evaluate(double x1, double x2) {
        	return Math.abs(x1-x2);
        }
    },
    GREATEROREQUAL(">=") {
            @Override public double evaluate(double x1, double x2) {
                return Math.abs(x2-x1);
            }
    };
  

    private final String operator;
    
    public double K;

    private BranchDistanceOperator(String operator) {
        this.operator = operator;
    }

    public abstract double evaluate(double x1, double x2);

    @Override public String toString() {
        return operator;
    }
}

