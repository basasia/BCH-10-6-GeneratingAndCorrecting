
package BCH;

import static java.lang.Math.abs;

/*
Class: BCHGenAndCorrect
Description: GUI and functions
Created: 3/11/2020.
Updated: 4/11/2020.
Authors: Asia Benyadilok
*/
public class BCHGenAndCorrect extends javax.swing.JFrame {

    /*
    initial method for GUI
    */
    public BCHGenAndCorrect() {
        initComponents();
        //clear all text
        jTextField2.setText("");
        jTextField1.setText("");
        jTextArea2.setText("");
    }
    
    //method: generate4Digits
    //parameters: String inputNum
    //Description: called when need to generate 4 digits
    public static String generate4Digits(String inputNum)
    {
        //initialize variables
        String outputMes = "";
        int[] numList = new int[10];
        //weights of each digit
        int[] d7 = {4,10,9,2,1,7};
        int[] d8 = {7,8,7,1,9,6};
        int[] d9 = {9,1,7,8,7,7};
        int[] d10 = {1,2,9,10,4,1};
        
        //remove white space from input
        inputNum = inputNum.replaceAll("\\s+","");
        
        //check the input length
        if(inputNum.length() != 6)
        {
            outputMes = "Error! input number is invalid";
        }
        else
        {
            try
            {
                for(int i=0;i<6;i++)
                {
                    //save input to array and calculate the weight
                    numList[i] = Integer.parseInt(String.valueOf(inputNum.charAt(i)));
                    numList[6] += numList[i]*d7[i];
                    numList[7] += numList[i]*d8[i];
                    numList[8] += numList[i]*d9[i];
                    numList[9] += numList[i]*d10[i];
                }    
                //output message for 4 digits
                outputMes = "Input "+inputNum+" 10 digits = ";
                  
                for(int i=0;i<10;i++)
                {   
                    if(i>5)
                    {
                        /*
                        //check if it is 10
                        if((numList[i]%11)==10)
                        {
                            outputMes = "unusable number";
                            break;
                        }
                        else
                        {
*/
                            //mod by 11
                            outputMes += (numList[i]%11);
                       // }
                    }
                    else
                    {
                         outputMes += numList[i];
                    }
                } 
                        
            }
            //in case error
            catch(Exception e)
            {
                outputMes = "Error! input number is invalid";
            }
        }
        
        return outputMes;
    }

    
    //method: BCHdecoder
    //parameters: String inputNum
    //Description: called when need to generate 4 syndromes
     public static String BCHdecoder(String inputNum)
    {
        //initialize variables
        String outputMes = "";
        int[] numList = new int[10];
        int s1 = 0;
        int s2 = 0;
        int s3 = 0;
        int s4 = 0;
        int a = 0;
        int b = 0;
        int P = 0;
        int Q = 0;
        int R = 0;
        int I = 0;
        int J = 0;
        int errorType = 0;
        
        //remove white space from input
        inputNum = inputNum.replaceAll("\\s+","");

        //check the length of input
        if(inputNum.length() != 10)
        {
            outputMes = "Error! input number is invalid";
        }
        else
        {
            try
            {
                for(int i=0;i<10;i++)
                {
                    //save integer to array and calculate syndromes with weights
                    numList[i] = Integer.parseInt(String.valueOf(inputNum.charAt(i)));
                    s1 += numList[i];
                    s2 += numList[i]*(i+1);
                    s3 += numList[i]*(Math.pow((i+1),2)%11);
                    s4 += numList[i]*(Math.pow((i+1),3)%11);
                    
                }
                
                    //after finished adding do mod 11
                    s1 = s1%11;
                    s2 = s2%11;
                    s3 = s3%11;
                    s4 = s4%11;



                //Check each syndromes for error detection
                
                //if all syndrome equal 0 means that no error
                if (s1 == 0 &&
                    s2 == 0 &&
                    s3 == 0 &&
                    s4 == 0)
                    {
                        //set error type to 0
                        errorType = 0;
                    }
                else
                {
                    //checking for error
                    //calculate P Q R
                    P = calModular((int)((Math.pow(s2,2))-(s1*s3)));
                    Q = calModular((s1*s4) - (s2*s3));
                    R = calModular((int)((Math.pow(s3,2)) - (s2*s4)));
                    
                    //if P Q R all zero
                    //there is one error
                    if (P == 0 && 
                        Q == 0 && 
                        R == 0)
                    {
                       //find error position
                       I = modularOperations(s2,s1,"/");
                       
                       //if position equal 0 means that there are more 3 error
                       if (I == 0)
                       {
                          //set error type to 3
                          errorType = 3; 
                       }
                       
                       //else find the position of error and correct it
                       else
                       {
                          //change to corret value
                          //error magnitude is s1
                          numList[I-1] = calModular((numList[I-1]-s1));
                          //set error type to 1
                          errorType = 1;
                       }
                       
                    }
                    //else P or Q or R not equal zero
                    //checking for double error
                    else
                    {
                       //calculate Q*Q - 4PR to use in quadratic fomula
                       int sqrtResult = modularSqrt(calModular((Q*Q)-(4*P*R)));
                       
                       //if result of sqrt return -1 means that no sqrt or error in division
                       //more than 2 error
                       if (sqrtResult == -1)
                       {
                           //set error type to 3;
                           errorType = 3;
                       }
                       //else can find result of sqrt
                       else
                       {
                           //find position i and j for double error
                           I = modularOperations(((-Q)+sqrtResult),(2*P),"/");
                           J = modularOperations(((-Q)-sqrtResult),(2*P),"/");
                           
                           //if i or j equal 0 or return -1 which mean that error in division
                           //more than 2 error
                           if((I ==0 || J == 0)||(I == -1 || J == -1))
                           {
                               //set error type to 3
                               errorType = 3;
                           }
                           //else can find value of i and j
                           else
                           {
                               //double error
                               //find error magnitudes a and b
                               b = modularOperations(((I*s1)-s2),I-J,"/");
                               a = modularOperations(s1,b,"-");
                               
                               //set error type to two
                               errorType = 2;
                               
                               //correct error at i and j with magnitude of a and b
                               numList[I-1] =  modularOperations(numList[I-1],a,"-");
                               numList[J-1] =  modularOperations(numList[J-1],b,"-");
                               
                               //if correct value equal to 10
                               //more than 2 error
                               if(numList[I-1] == 10 || numList[J-1] == 10)
                               {
                                   //set error type to 3
                                   errorType = 3;
                               }                             
                           }
                       }                                       
                    }                    
                }
                //print out message
                outputMes = ("Input: "+inputNum+"\nOuput: ");
                
                //print out correct value
                for(int i=0;i<10;i++)
                {
                    //if not more than 2 error print out correct value
                    if(errorType!=3)
                    {
                      outputMes+= numList[i];                        
                    }
                    //else print out question marks
                    else
                    {
                      outputMes+= "??";
                      break;
                    }
                }
                
                //check error type
                if (errorType == 0)
                {
                    outputMes += "\nNO ERROR";
                }
                //single error 
                else if(errorType == 1)
                {
                    outputMes+= "\nSINGLE ERROR i = "+I+", a = "+s1+", syndromes = "+s1+","+s2+","+s3+","+s4;   
                }
                //double error
                else if(errorType == 2)
                {
                    outputMes+= "\nDOUBLE ERROR i = "+I+", a = "+a+", j = "+J+", b = "+b+", syndromes = "+s1+","+s2+","+s3+","+s4+",  PQR = "+P+","+Q+","+R;   
                }
                //more than 2 error
                else if(errorType == 3)
                {
                    outputMes+= "\nMORE THAN 2 ERRORS  syndromes = "+s1+","+s2+","+s3+","+s4+",  PQR = "+P+","+Q+","+R;   
                }
  
            }
            //in case there is error during calculation
            catch(Exception e)
            {
                outputMes = "Error! input number is invalid";
            }
        }
        
        //return message
        return outputMes;
    }
   
     
    //method: modularOperations
    //parameters: int x, int y, String operation
    //Description: called when need to do modular operations    
    public static int modularOperations(int xIn,int yIn, String operation )
    {
     //initialise variables
     int x = xIn;
     int y = yIn;
     String op = operation;
     //table of inverse sqrt modular
     int [] yInverse = {1,6,4,3,9,2,8,7,5,10};
     int ans =0;
     String outMes ="";
        
        // incase y equals to 0 which is invalid 
        //return -1 means error
        if (y == 0 && op.equals("/"))
        {
                ans = -1;
        }
        else
        {
           //addition modular operations
           if (op.equals("+"))
           {
               ans = calModular(x+y);
           }
           
           //minus modular operations
           else if (op.equals("-"))
           {
               ans = calModular(x-y);
           }
           
           //multiplication modular operations
           else if (op.equals("*"))
           {
               ans = calModular(x*y);
           }
           
           //division modular operations
           else if (op.equals("/"))
           {
               
               //y out of range 0-10 or y is negative
               if(y >10 || y<1)
               {
                   //mod y first
                   //then calculate inverse of y by using table
                    //times with x then mod 11
                   ans = (calModular(x)*(yInverse[calModular(y)-1]))%11;
               }
               else
               {
                   //calculate inverse of y 
                   //times with x then mod 11
                   ans = (calModular(x)*(yInverse[abs(y)-1]))%11;
               }

           }
           //in case error
           else
           {
                ans = -1;
           }
          
        }
       
     //return answer
     return ans;
     
    }
     
     
    //method: calModular
    //parameters: String input
    //Description: called when need to calculate number under modular 11   
    public static int calModular(int input)
    {
        //initialize answer
        int ans =0;
        
        //if input greater or equal 0
        if (input>=0)
        {
            //mod by 11
             ans = input%11;
        }
        //else input is negative
        else
        {
            //change input to positive
             ans = Math.abs(input);
             
             //if ans more than 11
             if (ans > 11)
             {
                 //mod 11
                 ans = ans%11;
             }
             
             //if ans not 0
             if(ans !=0)
             {
                //make it positive under mod 11 
                ans = 11-ans;
             }
         }
       
       //return answer 
       return ans;
    }
         
    //method: modularSqrt
    //parameters: int input
    //Description: called when need to calculate sqrt under modular 11  
    public static int modularSqrt(int input)
    {
        //initialise variables
        int ans = 0;
        //sqrt table
        int [] sqrtTable = {1,-1,5,2,4,-1,-1,-1,3,-1};
        
        //if input is negative, or out of range 1-10
        if (input <= 0 || input > 10)
        {
            //return -1 means error
            ans = -1;
        }
        else
        {
           //return sqrt from the table
           ans = sqrtTable[input - 1]; 
        }
        
     //return answer   
     return ans;   
    }
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTextArea2 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField1.setText("jTextField1");

        jButton1.setText("generate1");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setAutoscrolls(false);

        jTextField2.setText("jTextField2");

        jButton2.setText("generate2");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jLabel1.setText("Enter 6 digits to generate 4 digits");

        jLabel2.setText("Enter 10 digits for BCH(10,6) decoder");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(166, 166, 166)
                            .addComponent(jLabel1))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(211, 211, 211)
                            .addComponent(jButton1))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(215, 215, 215)
                            .addComponent(jButton2))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(141, 141, 141)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 36, Short.MAX_VALUE)
                .addComponent(jTextArea2, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(41, 41, 41)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jTextArea2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    //method when button generate 1 is clicked
    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        //show output message
        //called generate4Digits method
        jTextArea2.setText(generate4Digits(jTextField1.getText()));
    }//GEN-LAST:event_jButton1MouseClicked

    //method to handle when button generate 2 is clicked
    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        //show output message
        //called BCHdecoder method
        jTextArea2.setText(BCHdecoder(jTextField2.getText()));
    }//GEN-LAST:event_jButton2MouseClicked

    //main method
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               BCHGenAndCorrect jframe = new BCHGenAndCorrect();
                jframe.setVisible(true);
                //set app title
                jframe.setTitle("BCH(10,6) generator and decoder app");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
