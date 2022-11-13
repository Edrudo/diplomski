package hr.fer.rgkk.transactions;

import org.bitcoinj.core.*;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import static org.bitcoinj.script.ScriptOpCodes.*;

public class PayToPubKeyHash extends ScriptTransaction {

    private final ECKey secretKey = new ECKey();

    public PayToPubKeyHash(WalletKit walletKit, NetworkParameters parameters) {
        super(walletKit, parameters);
    }

    @Override
    public Script createLockingScript() {
        // TODO: Create Locking script
        return new ScriptBuilder()
                .op(OP_DUP)
                .op(OP_HASH160)
                .data(secretKey.getPubKeyHash())
                .op(OP_EQUALVERIFY)
                .op(OP_CHECKSIG)
                .build();
    }

    @Override
    public Script createUnlockingScript(Transaction unsignedTransaction) {
        // TODO: Create Unlocking script
        byte[] signature  = sign(unsignedTransaction, secretKey).encodeToBitcoin();
        return new ScriptBuilder()
                .data(signature)
                .data(secretKey.getPubKey())
                .build();
    }
}
