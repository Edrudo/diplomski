// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./Timer.sol";

/// This contract represents most simple crowdfunding campaign.
/// This contract does not protects investors from not receiving goods
/// they were promised from crowdfunding owner. This kind of contract
/// might be suitable for campaigns that does not promise anything to the
/// investors except that they will start working on some project.
/// (e.g. almost all blockchain spinoffs.)
contract Crowdfunding {
    address private owner;

    Timer private timer;

    uint256 public goal;

    uint256 public endTimestamp;

    mapping(address => uint256) public investments;

    constructor(
        address _owner,
        Timer _timer,
        uint256 _goal,
        uint256 _endTimestamp
    ) {
        owner = (_owner == address(0) ? msg.sender : _owner);
        timer = _timer; // Not checking if this is correctly injected.
        goal = _goal;
        endTimestamp = _endTimestamp;
    }

    function invest() public payable {
        // TODO Your code here
        uint256 time = timer.getTime();
        require(time <= endTimestamp, "Time for investing is over.");
        uint256 funds = msg.value;
        investments[msg.sender] += funds;
        // revert("Not yet implemented");
    }

    function claimFunds() public {
        // TODO Your code here
        require(
            msg.sender == owner,
            "Only the contract owner can call this function."
        );
        uint256 time = timer.getTime();
        require(time > endTimestamp, "Investing is still in process.");
        uint256 totalFunds = address(this).balance;
        require(
            totalFunds >= goal,
            "You don't have the right to claim funds because the goal is not reached."
        );
        payable(owner).transfer(totalFunds);
        // revert("Not yet implemented");
    }

    function refund() public {
        // TODO Your code here
        uint256 time = timer.getTime();
        require(time > endTimestamp, "Investing is still in process.");
        uint256 totalFunds = address(this).balance;
        require(
            totalFunds < goal,
            "You can't have a refund because the goal is already reached."
        );
        uint256 refundValue = investments[msg.sender];
        investments[msg.sender] = 0;
        payable(msg.sender).transfer(refundValue);
        // revert("Not yet implemented");
    }
}
