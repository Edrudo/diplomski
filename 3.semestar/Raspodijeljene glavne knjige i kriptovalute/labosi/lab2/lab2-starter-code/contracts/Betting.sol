// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./BoxOracle.sol";

contract Betting {
    struct Player {
        uint8 id;
        string name;
        uint256 totalBetAmount;
        uint256 currCoef;
    }
    struct Bet {
        address bettor;
        uint256 amount;
        uint256 player_id;
        uint256 betCoef;
    }

    address private betMaker;
    BoxOracle public oracle;
    uint256 public minBetAmount;
    uint256 public maxBetAmount;
    uint256 public totalBetAmount;
    uint256 public thresholdAmount;
    bool public firstCheck = true;

    Bet[] private bets;
    Player public player_1;
    Player public player_2;

    bool private suspended = false;
    mapping(address => uint256) public balances;

    constructor(
        address _betMaker,
        string memory _player_1,
        string memory _player_2,
        uint256 _minBetAmount,
        uint256 _maxBetAmount,
        uint256 _thresholdAmount,
        BoxOracle _oracle
    ) {
        betMaker = (_betMaker == address(0) ? msg.sender : _betMaker);
        player_1 = Player(1, _player_1, 0, 200);
        player_2 = Player(2, _player_2, 0, 200);
        minBetAmount = _minBetAmount;
        maxBetAmount = _maxBetAmount;
        thresholdAmount = _thresholdAmount;
        oracle = _oracle;

        totalBetAmount = 0;
    }

    receive() external payable {}

    fallback() external payable {}

    function makeBet(uint8 _playerId) public payable {
        //TODO Your code here
        require(!suspended, "Betting is suspended.");
        require(msg.sender != betMaker, "The owner can't make bets.");
        uint256 amount = msg.value;
        require(amount >= minBetAmount, "The paid amount must be higher.");
        require(amount <= maxBetAmount, "The paid amount must be lower.");
        require(
            (_playerId == 1) || (_playerId == 2),
            "Player ID should be 1 or 2."
        );
        require(
            oracle.getWinner() == 0,
            "Bet should not be placed after match is finished."
        );
        bets.push(
            Bet(
                msg.sender,
                amount,
                _playerId,
                _playerId == 1 ? player_1.currCoef : player_2.currCoef
            )
        );
        totalBetAmount += amount;
        if (_playerId == 1) {
            player_1.totalBetAmount += amount;
        } else {
            player_2.totalBetAmount += amount;
        }
        balances[msg.sender] += amount;
        if (totalBetAmount > thresholdAmount) {
            if (firstCheck && (
                totalBetAmount == player_1.totalBetAmount ||
                totalBetAmount == player_2.totalBetAmount
                )) {
                firstCheck = false;
                suspended = true;

                return;
            }

            uint256 coef1 = (totalBetAmount * 100) / player_1.totalBetAmount;
            player_1.currCoef = coef1;

            uint256 coef2 = (totalBetAmount * 100) / player_2.totalBetAmount;
            player_2.currCoef = coef2;

        }
    }

    function claimSuspendedBets() public {
        require(
            suspended,
            "Betting isn't suspended so it's not possible to claim those bets."
        );

        payable(msg.sender).transfer(balances[msg.sender]);
        balances[msg.sender] = 0;
    }

    function claimWinningBets() public {
        require(!suspended);
        require(oracle.getWinner() != 0, "Betting is still in process.");

        uint256 dobitak = 0;
        for (uint256 i = 0; i < bets.length; i++) {
            if (
                (bets[i]).bettor == msg.sender &&
                oracle.getWinner() == (bets[i]).player_id
            ) {
                dobitak += (bets[i]).betCoef * (bets[i]).amount / 100;
            }
        }
        payable(msg.sender).transfer(dobitak);
        balances[msg.sender] = 0;
    }

    function claimLosingBets() public {
        require(oracle.getWinner() != 0, "Betting is still in process.");
        bool isplaceno_sve = true;
        for (uint256 i = 0; i < bets.length; i++) {
            if (balances[bets[i].bettor] > 0 && bets[i].player_id == oracle.getWinner()) {
                isplaceno_sve = false;
            }
        }
        require(isplaceno_sve, "It's still not possible to claim loing bets.");
        require(
            msg.sender == betMaker,
            "Only the bet maker can claim losing bets."
        );
        selfdestruct(payable(betMaker));
    }
}
