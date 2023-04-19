// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/pcmehlitz/projects/grammars/ContractSpec.g 2010-03-17 14:00:20

  package gov.nasa.jpf.aprop;  


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class ContractSpecParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "STRING", "INT", "REAL", "SIGN", "NUM", "ESCAPE", "STRING_PATTERN", "WS", "'||'", "'&&'", "'('", "')'", "'=='", "'!='", "'>'", "'>='", "'<'", "'<='", "'within'", "','", "'+-'", "'isEmpty'", "'notEmpty'", "'instanceof'", "'matches'", "'satisfies'", "'+'", "'-'", "'*'", "'/'", "'log10'", "'log'", "'^'", "'null'", "'return'", "'EPS'", "'old'"
    };
    public static final int SIGN=8;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int INT=6;
    public static final int ID=4;
    public static final int EOF=-1;
    public static final int NUM=9;
    public static final int STRING_PATTERN=11;
    public static final int REAL=7;
    public static final int T__30=30;
    public static final int T__19=19;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=12;
    public static final int T__33=33;
    public static final int T__16=16;
    public static final int T__34=34;
    public static final int T__15=15;
    public static final int ESCAPE=10;
    public static final int T__35=35;
    public static final int T__18=18;
    public static final int T__36=36;
    public static final int T__17=17;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int STRING=5;

    // delegates
    // delegators


        public ContractSpecParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ContractSpecParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return ContractSpecParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/pcmehlitz/projects/grammars/ContractSpec.g"; }


        ContractContext ctx;
        
        public ContractSpecParser (TokenStream input, ContractContext ctx) {
          this(input);
          
          this.ctx = ctx;
        }



    // $ANTLR start "contractSpec"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:27:1: contractSpec : contract ;
    public final void contractSpec() throws RecognitionException {
        Contract contract1 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:31:5: ( contract )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:32:5: contract
            {
            pushFollow(FOLLOW_contract_in_contractSpec67);
            contract1=contract();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               System.out.println(contract1); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "contractSpec"


    // $ANTLR start "contract"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:35:1: contract returns [Contract c] : c1= contractAnd ( '||' c2= contractAnd )* ;
    public final Contract contract() throws RecognitionException {
        Contract c = null;

        Contract c1 = null;

        Contract c2 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:36:5: (c1= contractAnd ( '||' c2= contractAnd )* )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:37:5: c1= contractAnd ( '||' c2= contractAnd )*
            {
            pushFollow(FOLLOW_contractAnd_in_contract118);
            c1=contractAnd();

            state._fsp--;
            if (state.failed) return c;
            if ( state.backtracking==0 ) {
               c=c1; 
            }
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:38:5: ( '||' c2= contractAnd )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:38:6: '||' c2= contractAnd
            	    {
            	    match(input,13,FOLLOW_13_in_contract145); if (state.failed) return c;
            	    pushFollow(FOLLOW_contractAnd_in_contract149);
            	    c2=contractAnd();

            	    state._fsp--;
            	    if (state.failed) return c;
            	    if ( state.backtracking==0 ) {
            	       c= new ContractOr(c,c2); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return c;
    }
    // $ANTLR end "contract"


    // $ANTLR start "contractAnd"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:42:1: contractAnd returns [Contract c] : c1= contractAtom ( '&&' c2= contractAtom )* ;
    public final Contract contractAnd() throws RecognitionException {
        Contract c = null;

        Contract c1 = null;

        Contract c2 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:43:5: (c1= contractAtom ( '&&' c2= contractAtom )* )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:44:5: c1= contractAtom ( '&&' c2= contractAtom )*
            {
            pushFollow(FOLLOW_contractAtom_in_contractAnd198);
            c1=contractAtom();

            state._fsp--;
            if (state.failed) return c;
            if ( state.backtracking==0 ) {
               c=c1; 
            }
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:45:5: ( '&&' c2= contractAtom )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==14) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:45:6: '&&' c2= contractAtom
            	    {
            	    match(input,14,FOLLOW_14_in_contractAnd224); if (state.failed) return c;
            	    pushFollow(FOLLOW_contractAtom_in_contractAnd228);
            	    c2=contractAtom();

            	    state._fsp--;
            	    if (state.failed) return c;
            	    if ( state.backtracking==0 ) {
            	       c= new ContractAnd(c,c2); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return c;
    }
    // $ANTLR end "contractAnd"


    // $ANTLR start "contractAtom"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:49:1: contractAtom returns [Contract c] : ( simpleContract | '(' contract ')' );
    public final Contract contractAtom() throws RecognitionException {
        Contract c = null;

        Contract simpleContract2 = null;

        Contract contract3 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:50:5: ( simpleContract | '(' contract ')' )
            int alt3=2;
            alt3 = dfa3.predict(input);
            switch (alt3) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:51:5: simpleContract
                    {
                    pushFollow(FOLLOW_simpleContract_in_contractAtom272);
                    simpleContract2=simpleContract();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c= simpleContract2; 
                    }

                    }
                    break;
                case 2 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:52:7: '(' contract ')'
                    {
                    match(input,15,FOLLOW_15_in_contractAtom300); if (state.failed) return c;
                    pushFollow(FOLLOW_contract_in_contractAtom302);
                    contract3=contract();

                    state._fsp--;
                    if (state.failed) return c;
                    match(input,16,FOLLOW_16_in_contractAtom304); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c= contract3; 
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return c;
    }
    // $ANTLR end "contractAtom"


    // $ANTLR start "simpleContract"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:55:1: simpleContract returns [Contract c] : e1= expr ( '==' e2= expr | '!=' e3= expr | '>' e4= expr | '>=' e5= expr | '<' e6= expr | '<=' e7= expr | 'within' e8= expr ( ',' e9= expr | '+-' e10= expr ) | 'isEmpty' | 'notEmpty' | 'instanceof' ID | 'matches' STRING | genericContract[$e1.o] ) ;
    public final Contract simpleContract() throws RecognitionException {
        Contract c = null;

        Token ID4=null;
        Token STRING5=null;
        Operand e1 = null;

        Operand e2 = null;

        Operand e3 = null;

        Operand e4 = null;

        Operand e5 = null;

        Operand e6 = null;

        Operand e7 = null;

        Operand e8 = null;

        Operand e9 = null;

        Operand e10 = null;

        Contract genericContract6 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:56:5: (e1= expr ( '==' e2= expr | '!=' e3= expr | '>' e4= expr | '>=' e5= expr | '<' e6= expr | '<=' e7= expr | 'within' e8= expr ( ',' e9= expr | '+-' e10= expr ) | 'isEmpty' | 'notEmpty' | 'instanceof' ID | 'matches' STRING | genericContract[$e1.o] ) )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:57:5: e1= expr ( '==' e2= expr | '!=' e3= expr | '>' e4= expr | '>=' e5= expr | '<' e6= expr | '<=' e7= expr | 'within' e8= expr ( ',' e9= expr | '+-' e10= expr ) | 'isEmpty' | 'notEmpty' | 'instanceof' ID | 'matches' STRING | genericContract[$e1.o] )
            {
            pushFollow(FOLLOW_expr_in_simpleContract349);
            e1=expr();

            state._fsp--;
            if (state.failed) return c;
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:58:5: ( '==' e2= expr | '!=' e3= expr | '>' e4= expr | '>=' e5= expr | '<' e6= expr | '<=' e7= expr | 'within' e8= expr ( ',' e9= expr | '+-' e10= expr ) | 'isEmpty' | 'notEmpty' | 'instanceof' ID | 'matches' STRING | genericContract[$e1.o] )
            int alt5=12;
            switch ( input.LA(1) ) {
            case 17:
                {
                alt5=1;
                }
                break;
            case 18:
                {
                alt5=2;
                }
                break;
            case 19:
                {
                alt5=3;
                }
                break;
            case 20:
                {
                alt5=4;
                }
                break;
            case 21:
                {
                alt5=5;
                }
                break;
            case 22:
                {
                alt5=6;
                }
                break;
            case 23:
                {
                alt5=7;
                }
                break;
            case 26:
                {
                alt5=8;
                }
                break;
            case 27:
                {
                alt5=9;
                }
                break;
            case 28:
                {
                alt5=10;
                }
                break;
            case 29:
                {
                alt5=11;
                }
                break;
            case 30:
                {
                alt5=12;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:59:5: '==' e2= expr
                    {
                    match(input,17,FOLLOW_17_in_simpleContract361); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract365);
                    e2=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.EQ(e1, e2); 
                    }

                    }
                    break;
                case 2 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:60:7: '!=' e3= expr
                    {
                    match(input,18,FOLLOW_18_in_simpleContract396); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract400);
                    e3=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.NE(e1, e3); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:61:7: '>' e4= expr
                    {
                    match(input,19,FOLLOW_19_in_simpleContract428); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract432);
                    e4=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.GT(e1, e4); 
                    }

                    }
                    break;
                case 4 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:62:7: '>=' e5= expr
                    {
                    match(input,20,FOLLOW_20_in_simpleContract461); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract465);
                    e5=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.GE(e1, e5); 
                    }

                    }
                    break;
                case 5 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:63:7: '<' e6= expr
                    {
                    match(input,21,FOLLOW_21_in_simpleContract493); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract497);
                    e6=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.LT(e1, e6); 
                    }

                    }
                    break;
                case 6 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:64:7: '<=' e7= expr
                    {
                    match(input,22,FOLLOW_22_in_simpleContract526); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract530);
                    e7=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.LE(e1, e7); 
                    }

                    }
                    break;
                case 7 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:65:7: 'within' e8= expr ( ',' e9= expr | '+-' e10= expr )
                    {
                    match(input,23,FOLLOW_23_in_simpleContract558); if (state.failed) return c;
                    pushFollow(FOLLOW_expr_in_simpleContract562);
                    e8=expr();

                    state._fsp--;
                    if (state.failed) return c;
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:66:9: ( ',' e9= expr | '+-' e10= expr )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==24) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==25) ) {
                        alt4=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return c;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:66:11: ',' e9= expr
                            {
                            match(input,24,FOLLOW_24_in_simpleContract575); if (state.failed) return c;
                            pushFollow(FOLLOW_expr_in_simpleContract579);
                            e9=expr();

                            state._fsp--;
                            if (state.failed) return c;
                            if ( state.backtracking==0 ) {
                               c = new Contract.Within( e1, e8, e9); 
                            }

                            }
                            break;
                        case 2 :
                            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:67:11: '+-' e10= expr
                            {
                            match(input,25,FOLLOW_25_in_simpleContract608); if (state.failed) return c;
                            pushFollow(FOLLOW_expr_in_simpleContract612);
                            e10=expr();

                            state._fsp--;
                            if (state.failed) return c;
                            if ( state.backtracking==0 ) {
                               c = new Contract.WithinCenter( e1, e8, e10); 
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:69:7: 'isEmpty'
                    {
                    match(input,26,FOLLOW_26_in_simpleContract645); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.IsEmpty( e1); 
                    }

                    }
                    break;
                case 9 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:70:7: 'notEmpty'
                    {
                    match(input,27,FOLLOW_27_in_simpleContract676); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.NotEmpty( e1); 
                    }

                    }
                    break;
                case 10 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:71:7: 'instanceof' ID
                    {
                    match(input,28,FOLLOW_28_in_simpleContract706); if (state.failed) return c;
                    ID4=(Token)match(input,ID,FOLLOW_ID_in_simpleContract708); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.InstanceOf( e1, (ID4!=null?ID4.getText():null)); 
                    }

                    }
                    break;
                case 11 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:72:7: 'matches' STRING
                    {
                    match(input,29,FOLLOW_29_in_simpleContract733); if (state.failed) return c;
                    STRING5=(Token)match(input,STRING,FOLLOW_STRING_in_simpleContract735); if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = new Contract.Matches( e1, (STRING5!=null?STRING5.getText():null)); 
                    }

                    }
                    break;
                case 12 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:73:7: genericContract[$e1.o]
                    {
                    pushFollow(FOLLOW_genericContract_in_simpleContract759);
                    genericContract6=genericContract(e1);

                    state._fsp--;
                    if (state.failed) return c;
                    if ( state.backtracking==0 ) {
                       c = genericContract6; 
                    }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return c;
    }
    // $ANTLR end "simpleContract"


    // $ANTLR start "genericContract"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:77:1: genericContract[Operand o] returns [Contract c] : 'satisfies' ID ( '(' (o1= expr ( ',' o2= expr )* )? ')' )? ;
    public final Contract genericContract(Operand o) throws RecognitionException {
        Contract c = null;

        Token ID7=null;
        Operand o1 = null;

        Operand o2 = null;



              ArrayList<Operand> args = null;
              String id = null;
            
        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:85:4: ( 'satisfies' ID ( '(' (o1= expr ( ',' o2= expr )* )? ')' )? )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:86:4: 'satisfies' ID ( '(' (o1= expr ( ',' o2= expr )* )? ')' )?
            {
            match(input,30,FOLLOW_30_in_genericContract821); if (state.failed) return c;
            ID7=(Token)match(input,ID,FOLLOW_ID_in_genericContract823); if (state.failed) return c;
            if ( state.backtracking==0 ) {
               id=(ID7!=null?ID7.getText():null); 
            }
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:87:5: ( '(' (o1= expr ( ',' o2= expr )* )? ')' )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==15) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:87:6: '(' (o1= expr ( ',' o2= expr )* )? ')'
                    {
                    match(input,15,FOLLOW_15_in_genericContract849); if (state.failed) return c;
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:88:9: (o1= expr ( ',' o2= expr )* )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( ((LA7_0>=ID && LA7_0<=REAL)||LA7_0==15||(LA7_0>=35 && LA7_0<=36)||(LA7_0>=38 && LA7_0<=41)) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:88:10: o1= expr ( ',' o2= expr )*
                            {
                            pushFollow(FOLLOW_expr_in_genericContract889);
                            o1=expr();

                            state._fsp--;
                            if (state.failed) return c;
                            if ( state.backtracking==0 ) {
                               args = new ArrayList<Operand>(); args.add(o1); 
                            }
                            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:89:10: ( ',' o2= expr )*
                            loop6:
                            do {
                                int alt6=2;
                                int LA6_0 = input.LA(1);

                                if ( (LA6_0==24) ) {
                                    alt6=1;
                                }


                                switch (alt6) {
                            	case 1 :
                            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:89:11: ',' o2= expr
                            	    {
                            	    match(input,24,FOLLOW_24_in_genericContract921); if (state.failed) return c;
                            	    pushFollow(FOLLOW_expr_in_genericContract925);
                            	    o2=expr();

                            	    state._fsp--;
                            	    if (state.failed) return c;
                            	    if ( state.backtracking==0 ) {
                            	       args.add(o2); 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop6;
                                }
                            } while (true);


                            }
                            break;

                    }

                    match(input,16,FOLLOW_16_in_genericContract970); if (state.failed) return c;

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {

                      c = new Satisfies(ctx, id, o, args);
                  
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return c;
    }
    // $ANTLR end "genericContract"


    // $ANTLR start "expr"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:97:1: expr returns [Operand o] : o1= mult ( '+' o2= mult | '-' o3= mult )* ;
    public final Operand expr() throws RecognitionException {
        Operand o = null;

        Operand o1 = null;

        Operand o2 = null;

        Operand o3 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:98:5: (o1= mult ( '+' o2= mult | '-' o3= mult )* )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:99:5: o1= mult ( '+' o2= mult | '-' o3= mult )*
            {
            pushFollow(FOLLOW_mult_in_expr1031);
            o1=mult();

            state._fsp--;
            if (state.failed) return o;
            if ( state.backtracking==0 ) {
               o = o1; 
            }
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:100:5: ( '+' o2= mult | '-' o3= mult )*
            loop9:
            do {
                int alt9=3;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==31) ) {
                    alt9=1;
                }
                else if ( (LA9_0==32) ) {
                    alt9=2;
                }


                switch (alt9) {
            	case 1 :
            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:100:7: '+' o2= mult
            	    {
            	    match(input,31,FOLLOW_31_in_expr1066); if (state.failed) return o;
            	    pushFollow(FOLLOW_mult_in_expr1070);
            	    o2=mult();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	       o = new Expr.Plus(o, o2); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:101:7: '-' o3= mult
            	    {
            	    match(input,32,FOLLOW_32_in_expr1099); if (state.failed) return o;
            	    pushFollow(FOLLOW_mult_in_expr1103);
            	    o3=mult();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	       o = new Expr.Minus(o, o3); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "expr"


    // $ANTLR start "mult"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:105:1: mult returns [Operand o] : o1= log ( '*' o2= log | '/' o3= log )* ;
    public final Operand mult() throws RecognitionException {
        Operand o = null;

        Operand o1 = null;

        Operand o2 = null;

        Operand o3 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:106:5: (o1= log ( '*' o2= log | '/' o3= log )* )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:107:5: o1= log ( '*' o2= log | '/' o3= log )*
            {
            pushFollow(FOLLOW_log_in_mult1165);
            o1=log();

            state._fsp--;
            if (state.failed) return o;
            if ( state.backtracking==0 ) {
               o = o1; 
            }
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:108:5: ( '*' o2= log | '/' o3= log )*
            loop10:
            do {
                int alt10=3;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==33) ) {
                    alt10=1;
                }
                else if ( (LA10_0==34) ) {
                    alt10=2;
                }


                switch (alt10) {
            	case 1 :
            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:108:7: '*' o2= log
            	    {
            	    match(input,33,FOLLOW_33_in_mult1201); if (state.failed) return o;
            	    pushFollow(FOLLOW_log_in_mult1205);
            	    o2=log();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	       o = new Expr.Mult(o,o2); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:109:7: '/' o3= log
            	    {
            	    match(input,34,FOLLOW_34_in_mult1235); if (state.failed) return o;
            	    pushFollow(FOLLOW_log_in_mult1239);
            	    o3=log();

            	    state._fsp--;
            	    if (state.failed) return o;
            	    if ( state.backtracking==0 ) {
            	       o = new Expr.Div(o, o3); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "mult"


    // $ANTLR start "log"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:113:1: log returns [Operand o] : (o1= exp | 'log10' '(' o3= exp ')' | 'log' '(' o2= exp ')' );
    public final Operand log() throws RecognitionException {
        Operand o = null;

        Operand o1 = null;

        Operand o3 = null;

        Operand o2 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:114:5: (o1= exp | 'log10' '(' o3= exp ')' | 'log' '(' o2= exp ')' )
            int alt11=3;
            switch ( input.LA(1) ) {
            case ID:
            case STRING:
            case INT:
            case REAL:
            case 15:
            case 38:
            case 39:
            case 40:
            case 41:
                {
                alt11=1;
                }
                break;
            case 35:
                {
                alt11=2;
                }
                break;
            case 36:
                {
                alt11=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return o;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:115:5: o1= exp
                    {
                    pushFollow(FOLLOW_exp_in_log1299);
                    o1=exp();

                    state._fsp--;
                    if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = o1; 
                    }

                    }
                    break;
                case 2 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:116:7: 'log10' '(' o3= exp ')'
                    {
                    match(input,35,FOLLOW_35_in_log1335); if (state.failed) return o;
                    match(input,15,FOLLOW_15_in_log1337); if (state.failed) return o;
                    pushFollow(FOLLOW_exp_in_log1341);
                    o3=exp();

                    state._fsp--;
                    if (state.failed) return o;
                    match(input,16,FOLLOW_16_in_log1343); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Expr.Log10(o3); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:117:7: 'log' '(' o2= exp ')'
                    {
                    match(input,36,FOLLOW_36_in_log1361); if (state.failed) return o;
                    match(input,15,FOLLOW_15_in_log1363); if (state.failed) return o;
                    pushFollow(FOLLOW_exp_in_log1367);
                    o2=exp();

                    state._fsp--;
                    if (state.failed) return o;
                    match(input,16,FOLLOW_16_in_log1369); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Expr.Log(o2); 
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "log"


    // $ANTLR start "exp"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:120:1: exp returns [Operand o] : o1= fun ( '^' o2= fun )? ;
    public final Operand exp() throws RecognitionException {
        Operand o = null;

        Operand o1 = null;

        Operand o2 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:121:5: (o1= fun ( '^' o2= fun )? )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:122:5: o1= fun ( '^' o2= fun )?
            {
            pushFollow(FOLLOW_fun_in_exp1413);
            o1=fun();

            state._fsp--;
            if (state.failed) return o;
            if ( state.backtracking==0 ) {
               o = o1; 
            }
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:123:5: ( '^' o2= fun )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==37) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:123:6: '^' o2= fun
                    {
                    match(input,37,FOLLOW_37_in_exp1448); if (state.failed) return o;
                    pushFollow(FOLLOW_fun_in_exp1452);
                    o2=fun();

                    state._fsp--;
                    if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Expr.Pow(o, o2); 
                    }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "exp"


    // $ANTLR start "fun"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:127:1: fun returns [Operand o] : ( atom | ( ID '(' (o1= expr ( ',' o2= expr )* )? ')' ) );
    public final Operand fun() throws RecognitionException {
        Operand o = null;

        Token ID9=null;
        Operand o1 = null;

        Operand o2 = null;

        Operand atom8 = null;



              ArrayList<Operand> args = new ArrayList<Operand>();
              String id = "?";
            
        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:132:5: ( atom | ( ID '(' (o1= expr ( ',' o2= expr )* )? ')' ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=STRING && LA15_0<=REAL)||LA15_0==15||(LA15_0>=38 && LA15_0<=41)) ) {
                alt15=1;
            }
            else if ( (LA15_0==ID) ) {
                int LA15_2 = input.LA(2);

                if ( (LA15_2==15) ) {
                    alt15=2;
                }
                else if ( (LA15_2==EOF||(LA15_2>=13 && LA15_2<=14)||(LA15_2>=16 && LA15_2<=34)||LA15_2==37) ) {
                    alt15=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return o;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return o;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:133:5: atom
                    {
                    pushFollow(FOLLOW_atom_in_fun1521);
                    atom8=atom();

                    state._fsp--;
                    if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = atom8; 
                    }

                    }
                    break;
                case 2 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:134:7: ( ID '(' (o1= expr ( ',' o2= expr )* )? ')' )
                    {
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:134:7: ( ID '(' (o1= expr ( ',' o2= expr )* )? ')' )
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:134:9: ID '(' (o1= expr ( ',' o2= expr )* )? ')'
                    {
                    ID9=(Token)match(input,ID,FOLLOW_ID_in_fun1562); if (state.failed) return o;
                    match(input,15,FOLLOW_15_in_fun1564); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       id=(ID9!=null?ID9.getText():null); 
                    }
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:135:9: (o1= expr ( ',' o2= expr )* )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( ((LA14_0>=ID && LA14_0<=REAL)||LA14_0==15||(LA14_0>=35 && LA14_0<=36)||(LA14_0>=38 && LA14_0<=41)) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:135:10: o1= expr ( ',' o2= expr )*
                            {
                            pushFollow(FOLLOW_expr_in_fun1601);
                            o1=expr();

                            state._fsp--;
                            if (state.failed) return o;
                            if ( state.backtracking==0 ) {
                               args = new ArrayList<Operand>(); args.add(o1); 
                            }
                            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:136:10: ( ',' o2= expr )*
                            loop13:
                            do {
                                int alt13=2;
                                int LA13_0 = input.LA(1);

                                if ( (LA13_0==24) ) {
                                    alt13=1;
                                }


                                switch (alt13) {
                            	case 1 :
                            	    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:136:11: ',' o2= expr
                            	    {
                            	    match(input,24,FOLLOW_24_in_fun1635); if (state.failed) return o;
                            	    pushFollow(FOLLOW_expr_in_fun1639);
                            	    o2=expr();

                            	    state._fsp--;
                            	    if (state.failed) return o;
                            	    if ( state.backtracking==0 ) {
                            	       args.add(o2); 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop13;
                                }
                            } while (true);


                            }
                            break;

                    }

                    match(input,16,FOLLOW_16_in_fun1689); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Expr.Func(id,args); 
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "fun"


    // $ANTLR start "atom"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:143:1: atom returns [Operand o] : ( 'null' | 'return' | 'EPS' | numOrVar | STRING | 'old' '(' e1= expr ')' | '(' e2= expr ')' );
    public final Operand atom() throws RecognitionException {
        Operand o = null;

        Token STRING11=null;
        Operand e1 = null;

        Operand e2 = null;

        Operand numOrVar10 = null;


        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:143:25: ( 'null' | 'return' | 'EPS' | numOrVar | STRING | 'old' '(' e1= expr ')' | '(' e2= expr ')' )
            int alt16=7;
            switch ( input.LA(1) ) {
            case 38:
                {
                alt16=1;
                }
                break;
            case 39:
                {
                alt16=2;
                }
                break;
            case 40:
                {
                alt16=3;
                }
                break;
            case ID:
            case INT:
            case REAL:
                {
                alt16=4;
                }
                break;
            case STRING:
                {
                alt16=5;
                }
                break;
            case 41:
                {
                alt16=6;
                }
                break;
            case 15:
                {
                alt16=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return o;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:144:5: 'null'
                    {
                    match(input,38,FOLLOW_38_in_atom1744); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = Operand.NULL; 
                    }

                    }
                    break;
                case 2 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:145:7: 'return'
                    {
                    match(input,39,FOLLOW_39_in_atom1780); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Expr.Result(); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:146:7: 'EPS'
                    {
                    match(input,40,FOLLOW_40_in_atom1812); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = Operand.EPS; 
                    }

                    }
                    break;
                case 4 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:147:7: numOrVar
                    {
                    pushFollow(FOLLOW_numOrVar_in_atom1847);
                    numOrVar10=numOrVar();

                    state._fsp--;
                    if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = numOrVar10; 
                    }

                    }
                    break;
                case 5 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:148:7: STRING
                    {
                    STRING11=(Token)match(input,STRING,FOLLOW_STRING_in_atom1879); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Operand.Const((STRING11!=null?STRING11.getText():null)); 
                    }

                    }
                    break;
                case 6 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:149:7: 'old' '(' e1= expr ')'
                    {
                    match(input,41,FOLLOW_41_in_atom1913); if (state.failed) return o;
                    match(input,15,FOLLOW_15_in_atom1915); if (state.failed) return o;
                    pushFollow(FOLLOW_expr_in_atom1919);
                    e1=expr();

                    state._fsp--;
                    if (state.failed) return o;
                    match(input,16,FOLLOW_16_in_atom1921); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Expr.Old(e1); 
                    }

                    }
                    break;
                case 7 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:150:7: '(' e2= expr ')'
                    {
                    match(input,15,FOLLOW_15_in_atom1940); if (state.failed) return o;
                    pushFollow(FOLLOW_expr_in_atom1944);
                    e2=expr();

                    state._fsp--;
                    if (state.failed) return o;
                    match(input,16,FOLLOW_16_in_atom1946); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = e2; 
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "atom"


    // $ANTLR start "numOrVar"
    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:153:1: numOrVar returns [Operand o] : (i2= ID | INT | REAL ) ;
    public final Operand numOrVar() throws RecognitionException {
        Operand o = null;

        Token i2=null;
        Token INT12=null;
        Token REAL13=null;

        try {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:153:29: ( (i2= ID | INT | REAL ) )
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:154:5: (i2= ID | INT | REAL )
            {
            // /Users/pcmehlitz/projects/grammars/ContractSpec.g:154:5: (i2= ID | INT | REAL )
            int alt17=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt17=1;
                }
                break;
            case INT:
                {
                alt17=2;
                }
                break;
            case REAL:
                {
                alt17=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return o;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:154:6: i2= ID
                    {
                    i2=(Token)match(input,ID,FOLLOW_ID_in_numOrVar1986); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Operand.VarRef((i2!=null?i2.getText():null)); 
                    }

                    }
                    break;
                case 2 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:155:7: INT
                    {
                    INT12=(Token)match(input,INT,FOLLOW_INT_in_numOrVar2022); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Operand.Const(new Integer((INT12!=null?INT12.getText():null))); 
                    }

                    }
                    break;
                case 3 :
                    // /Users/pcmehlitz/projects/grammars/ContractSpec.g:156:7: REAL
                    {
                    REAL13=(Token)match(input,REAL,FOLLOW_REAL_in_numOrVar2059); if (state.failed) return o;
                    if ( state.backtracking==0 ) {
                       o = new Operand.Const(new Double((REAL13!=null?REAL13.getText():null))); 
                    }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return o;
    }
    // $ANTLR end "numOrVar"

    // $ANTLR start synpred3_ContractSpec
    public final void synpred3_ContractSpec_fragment() throws RecognitionException {   
        // /Users/pcmehlitz/projects/grammars/ContractSpec.g:51:5: ( simpleContract )
        // /Users/pcmehlitz/projects/grammars/ContractSpec.g:51:5: simpleContract
        {
        pushFollow(FOLLOW_simpleContract_in_synpred3_ContractSpec272);
        simpleContract();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_ContractSpec

    // Delegated rules

    public final boolean synpred3_ContractSpec() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_ContractSpec_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\15\uffff";
    static final String DFA3_eofS =
        "\15\uffff";
    static final String DFA3_minS =
        "\1\4\10\uffff\1\0\3\uffff";
    static final String DFA3_maxS =
        "\1\51\10\uffff\1\0\3\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\1\1\12\uffff\1\2";
    static final String DFA3_specialS =
        "\11\uffff\1\0\3\uffff}>";
    static final String[] DFA3_transitionS = {
            "\4\1\7\uffff\1\11\23\uffff\2\1\1\uffff\4\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "49:1: contractAtom returns [Contract c] : ( simpleContract | '(' contract ')' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA3_9 = input.LA(1);

                         
                        int index3_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_ContractSpec()) ) {s = 1;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index3_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 3, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_contract_in_contractSpec67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_contractAnd_in_contract118 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_contract145 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_contractAnd_in_contract149 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_contractAtom_in_contractAnd198 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_contractAnd224 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_contractAtom_in_contractAnd228 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_simpleContract_in_contractAtom272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_contractAtom300 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_contract_in_contractAtom302 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_contractAtom304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_simpleContract349 = new BitSet(new long[]{0x000000007CFE0000L});
    public static final BitSet FOLLOW_17_in_simpleContract361 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_simpleContract396 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_simpleContract428 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_simpleContract461 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_simpleContract493 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_simpleContract526 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_simpleContract558 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract562 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_24_in_simpleContract575 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_simpleContract608 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_simpleContract612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_simpleContract645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_simpleContract676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_simpleContract706 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_simpleContract708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_simpleContract733 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_STRING_in_simpleContract735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericContract_in_simpleContract759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_genericContract821 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_genericContract823 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_genericContract849 = new BitSet(new long[]{0x000003D8000180F0L});
    public static final BitSet FOLLOW_expr_in_genericContract889 = new BitSet(new long[]{0x0000000001010000L});
    public static final BitSet FOLLOW_24_in_genericContract921 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_genericContract925 = new BitSet(new long[]{0x0000000001010000L});
    public static final BitSet FOLLOW_16_in_genericContract970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_in_expr1031 = new BitSet(new long[]{0x0000000180000002L});
    public static final BitSet FOLLOW_31_in_expr1066 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_mult_in_expr1070 = new BitSet(new long[]{0x0000000180000002L});
    public static final BitSet FOLLOW_32_in_expr1099 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_mult_in_expr1103 = new BitSet(new long[]{0x0000000180000002L});
    public static final BitSet FOLLOW_log_in_mult1165 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_33_in_mult1201 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_log_in_mult1205 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_34_in_mult1235 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_log_in_mult1239 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_exp_in_log1299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_log1335 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_log1337 = new BitSet(new long[]{0x000003C0000080F0L});
    public static final BitSet FOLLOW_exp_in_log1341 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_log1343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_log1361 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_log1363 = new BitSet(new long[]{0x000003C0000080F0L});
    public static final BitSet FOLLOW_exp_in_log1367 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_log1369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fun_in_exp1413 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_37_in_exp1448 = new BitSet(new long[]{0x000003C0000080F0L});
    public static final BitSet FOLLOW_fun_in_exp1452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_fun1521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fun1562 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_fun1564 = new BitSet(new long[]{0x000003D8000180F0L});
    public static final BitSet FOLLOW_expr_in_fun1601 = new BitSet(new long[]{0x0000000001010000L});
    public static final BitSet FOLLOW_24_in_fun1635 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_fun1639 = new BitSet(new long[]{0x0000000001010000L});
    public static final BitSet FOLLOW_16_in_fun1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_atom1744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_atom1780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_atom1812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numOrVar_in_atom1847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_atom1879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_atom1913 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_atom1915 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_atom1919 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_atom1921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_atom1940 = new BitSet(new long[]{0x000003D8000080F0L});
    public static final BitSet FOLLOW_expr_in_atom1944 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_atom1946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_numOrVar1986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_numOrVar2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_numOrVar2059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleContract_in_synpred3_ContractSpec272 = new BitSet(new long[]{0x0000000000000002L});

}