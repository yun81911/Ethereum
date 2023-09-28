package com.example.demo.contract;

import com.example.demo.util.Constants;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.model.HelloWorld;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class HelloWorldDeploy {

    public static void main(String[] args) throws Exception {
        // 启动客户端
        Web3j web3j = Web3j.build(new HttpService(Constants.URL));
        Credentials credentials = WalletUtils.loadCredentials(Constants.PASSWORD, Constants.SOURCE);
        System.out.println("getCredentialsAddress : " + credentials.getAddress());

        // 部署合约
        HelloWorld contract = HelloWorld.deploy(web3j, credentials, new DefaultGasProvider()).send();
        System.out.println("getContractAddress : " + contract.getContractAddress());
        // rewrite: contractAddress ----> application.properties
    }

     /**
     * 根据交易hash查询交易信息
     * @param web3j
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static  void pendingTransactionFlowable(Web3j web3j) throws ExecutionException, InterruptedException {

        EthTransaction ethTx = web3j
                .ethGetTransactionByHash("0xbf56c46fcadf382d5d8fc5c617828220ba0441912cf6020759c51f7d3961ff60")
                .sendAsync()
                .get();

        org.web3j.protocol.core.methods.response.Transaction tx = ethTx
                .getTransaction()
                .get();

        String blockHash = tx.getBlockHash();
        BigInteger blockNumber = tx.getBlockNumber();
        String from = tx.getFrom();
        String to = tx.getTo();
        BigInteger amount = tx.getValue();

        // 交易信息
        System.out.println("交易地址 hash ："+ tx.getHash());
        System.out.println("交易区块 ："+ blockNumber  );
        System.out.println("转出 account："+from);
        System.out.println("转入（合约） account："+to);
        System.out.println("交易value："+amount);
        System.out.println("交易input："+tx.getInput());

        // 根据区块hash 查询区块信息
        EthBlock ethBlock = web3j
                .ethGetBlockByHash(blockHash, true)
                .sendAsync()
                .get();

        EthBlock.Block blockByHash = ethBlock.getBlock();

        System.out.println("区块hash ：" + blockHash);


        // 根据区块number 查询区块信息
        DefaultBlockParameter blockParameter = DefaultBlockParameter.valueOf(blockNumber);
        ethBlock = web3j
                .ethGetBlockByNumber(blockParameter, true)
                .sendAsync()
                .get();

        EthBlock.Block bk = ethBlock.getBlock();

        System.out.println("区块 number： " + blockNumber);
        System.out.println("区块 hash："+ bk.getHash() );
        System.out.println("区块 number："+ blockByHash.getNumber());
        System.out.println("区块 parent hashes："+blockByHash.getParentHash());


        // 根据区块 查询交易信息
        for(EthBlock.TransactionResult<?> txResult: blockByHash.getTransactions()) {
            EthBlock.TransactionObject txObject = (EthBlock.TransactionObject) txResult;
            // 交易信息
            System.out.println("转出 account："+ txObject.getFrom() );
            System.out.println("转入（合约）："+ txObject.getTo() );
            System.out.println("交易value："+ txObject.getValue() );
            System.out.println("交易input："+ txObject.getInput() );
        }


    }
}
