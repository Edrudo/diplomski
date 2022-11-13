package hr.fer.rgkk.transactions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.security.SecureRandom;

import static org.bitcoinj.script.ScriptOpCodes.*;

public class CoinToss extends ScriptTransaction {
    // Alice's private key
    private final ECKey aliceKey;
    // Alice's nonce
    private final byte[] aliceNonce;
    // Bob's private key
    private final ECKey bobKey;
    // Bob's nonce
    private final byte[] bobNonce;
    // Key used in unlocking script to select winning player.
    private final ECKey winningPlayerKey;

    private CoinToss(
            WalletKit walletKit, NetworkParameters parameters,
            ECKey aliceKey, byte[] aliceNonce,
            ECKey bobKey, byte[] bobNonce,
            ECKey winningPlayerKey
    ) {
        super(walletKit, parameters);
        this.aliceKey = aliceKey;
        this.aliceNonce = aliceNonce;
        this.bobKey = bobKey;
        this.bobNonce = bobNonce;
        this.winningPlayerKey = winningPlayerKey;
    }

    @Override
    public Script createLockingScript() {
        // TODO: Create Locking script
        long sixteen_bytes = 2 ^ (16 * 8) + 1;
        long sixteen = 16;

        ScriptBuilder sb = new ScriptBuilder();

        sb.op(OP_2DUP); // sig Rb Ra Rb Ra

        sb.op(OP_HASH160); // sig Rb Ra Rb C'a
        sb.data(Utils.sha256hash160(aliceNonce)); // sig Rb Ra Rb C'a Ca
        sb.op(OP_EQUALVERIFY); // fail OR sig Rb Ra Rb

        sb.op(OP_HASH160); // sig Rb Ra C'b
        sb.data(Utils.sha256hash160(bobNonce)); // sig Rb Ra C'b Cb
        sb.op(OP_EQUALVERIFY); // fail OR sig Rb Ra

        // ensured that Ra and Rb match Ca and Cb, respectively

        //sb.op(OP_SWAP); // sig Ra Rb
        sb.op(OP_SIZE); // sig Rb Ra lenA
        sb.op(OP_NIP); // sig Rb lenA
        sb.number(sixteen); // sig Rb lenA 16
        sb.op(OP_SUB); // sig Rb Na

        sb.op(OP_SWAP); // sig Na Rb
        sb.op(OP_SIZE); // sig Na Rb lenB
        sb.number(sixteen); // sig Na Rb lenB 16
        sb.op(OP_SUB); // sig Na Rb Nb
        sb.op(OP_NIP); // sig Na Nb

        // calculated Na and Nb from Ra and Rb, respectively

        sb.op(OP_DUP);
        sb.smallNum(0);

        sb.op(OP_EQUAL);
        sb.op(OP_IF);

        sb.op(OP_EQUAL);
        // determined the winner Alice=1=Tails, Bob=0=Heads
        sb.op(OP_0NOTEQUAL); // sig True if Alice, sig False if Bob
        sb.op(OP_IF);
        sb.data(aliceKey.getPubKey()); // sig bobKey
        sb.op(OP_ELSE);
        sb.data(bobKey.getPubKey()); // sig aliceKey
        sb.op(OP_ENDIF);


        sb.op(OP_ELSE);
        sb.op(OP_EQUAL);
        // determined the winner Alice=1=Tails, Bob=0=Heads
        sb.op(OP_0NOTEQUAL); // sig True if Alice, sig False if Bob
        sb.op(OP_IF);
        sb.data(bobKey.getPubKey()); // sig bobKey
        sb.op(OP_ELSE);
        sb.data(bobKey.getPubKey()); // sig aliceKey
        sb.op(OP_ENDIF);

//        sb.op(OP_EQUAL);
//        sb.op(OP_0NOTEQUAL); // sig True if Alice, sig False if Bob
//        sb.op(OP_IF);
//        sb.data(bobKey.getPubKey()); // sig bobKey
//        sb.op(OP_ELSE);
//        sb.data(aliceKey.getPubKey()); // sig aliceKey
//        sb.op(OP_ENDIF);

        sb.op(OP_ENDIF);


        sb.op(OP_CHECKSIG);

        return sb.build();
        //throw new UnsupportedOperationException();
    }

    @Override
    public Script createUnlockingScript(Transaction unsignedTransaction) {
        TransactionSignature signature = sign(unsignedTransaction, winningPlayerKey);

        return new ScriptBuilder()
                .data(signature.encodeToBitcoin())
                .data(bobNonce)
                .data(aliceNonce)
                .build();
    }

    public static CoinToss of(
            WalletKit walletKit, NetworkParameters parameters,
            CoinTossChoice aliceChoice, CoinTossChoice bobChoice,
            WinningPlayer winningPlayer
    ) {
        byte[] aliceNonce = randomBytes(16 + aliceChoice.value);
        byte[] bobNonce = randomBytes(16 + bobChoice.value);

        ECKey aliceKey = randKey();
        ECKey bobKey = randKey();

        // Alice is TAIL, bob is HEAD
        ECKey winningPlayerKey = WinningPlayer.TAIL == winningPlayer ? aliceKey : bobKey;

        return new CoinToss(
                walletKit, parameters,
                aliceKey, aliceNonce,
                bobKey, bobNonce,
                winningPlayerKey
        );
    }

    private static byte[] randomBytes(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    public enum WinningPlayer {
        TAIL, HEAD
    }

    public enum CoinTossChoice {

        ZERO(0),
        ONE(1);

        public final int value;

        CoinTossChoice(int value) {
            this.value = value;
        }
    }
}

