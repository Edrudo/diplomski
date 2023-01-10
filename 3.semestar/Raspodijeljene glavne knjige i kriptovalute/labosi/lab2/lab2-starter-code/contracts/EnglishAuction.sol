// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./Auction.sol";

contract EnglishAuction is Auction {
    uint256 internal highestBid;
    uint256 internal initialPrice;
    uint256 internal biddingPeriod;
    uint256 internal lastBidTimestamp;
    uint256 internal minimumPriceIncrement;

    address internal highestBidder;

    constructor(
        address _sellerAddress,
        address _judgeAddress,
        Timer _timer,
        uint256 _initialPrice,
        uint256 _biddingPeriod,
        uint256 _minimumPriceIncrement
    ) Auction(_sellerAddress, _judgeAddress, _timer) {
        initialPrice = _initialPrice;
        biddingPeriod = _biddingPeriod;
        minimumPriceIncrement = _minimumPriceIncrement;

        // Start the auction at contract creation.
        lastBidTimestamp = time();
    }

    function bid() public payable {
        uint256 cijena = 0;
        if (highestBid != 0 && highestBidder != address(0)) {
            cijena = highestBid + minimumPriceIncrement;
        } else {
            cijena = initialPrice;
        }
        require(msg.value >= cijena);
        require((time() - lastBidTimestamp) < biddingPeriod);

        bool check_if = false;
        address prethodni_najveci_ulagac = address(0);
        uint256 prethodna_najvisa_ponuda = 0;
        if (highestBid != 0 && highestBidder != address(0)) {
            prethodni_najveci_ulagac = highestBidder;
            prethodna_najvisa_ponuda = address(this).balance - msg.value;
            check_if = true;
        }

        highestBidder = msg.sender;
        highestBid = msg.value;
        lastBidTimestamp = time();

        if (check_if) {
            payable(prethodni_najveci_ulagac).transfer(
                prethodna_najvisa_ponuda
            );
        }
    }

    function getHighestBidder() public view override returns (address) {
        if (time() - lastBidTimestamp < biddingPeriod) {
            return address(0);
        }

        return highestBidder;
    }

    function enableRefunds() public {
        outcome = Outcome.NOT_SUCCESSFUL;
    }
}
