// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./Auction.sol";

contract EnglishAuction is Auction {

    uint internal highestBid;
    uint internal initialPrice;
    uint internal biddingPeriod;
    uint internal lastBidTimestamp;
    uint internal minimumPriceIncrement;

    address internal highestBidder;

    constructor(
        address _sellerAddress,
        address _judgeAddress,
        Timer _timer,
        uint _initialPrice,
        uint _biddingPeriod,
        uint _minimumPriceIncrement
    ) Auction(_sellerAddress, _judgeAddress, _timer) {
        initialPrice = _initialPrice;
        biddingPeriod = _biddingPeriod;
        minimumPriceIncrement = _minimumPriceIncrement;

        // Start the auction at contract creation.
        lastBidTimestamp = time();
    }

    function bid() public payable {
        require(time() - lastBidTimestamp < biddingPeriod);
        require(msg.value >= initialPrice && 
                msg.value > highestBid && 
                msg.value - highestBid >= minimumPriceIncrement
        );

        payable(highestBidder).transfer(highestBid);

        highestBid = msg.value;
        lastBidTimestamp = block.timestamp;
        highestBidder = msg.sender;
    }

    function getHighestBidder() override public view returns (address) {
        if(time() - lastBidTimestamp < biddingPeriod){
            return address(0);
        }

        return highestBidder;
    }

    function enableRefunds() public {
        outcome = Outcome.NOT_SUCCESSFUL;
    }

}