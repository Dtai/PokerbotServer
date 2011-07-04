/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package bots.mctsbot.ai.opponentmodels.weka;

import org.apache.log4j.Logger;

import bots.mctsbot.client.common.gamestate.DetailedHoldemTableState;
import bots.mctsbot.client.common.gamestate.GameState;
import bots.mctsbot.client.common.gamestate.GameStateVisitor;
import bots.mctsbot.client.common.gamestate.modifiers.AllInState;
import bots.mctsbot.client.common.gamestate.modifiers.BetState;
import bots.mctsbot.client.common.gamestate.modifiers.BlindState;
import bots.mctsbot.client.common.gamestate.modifiers.CallState;
import bots.mctsbot.client.common.gamestate.modifiers.CheckState;
import bots.mctsbot.client.common.gamestate.modifiers.ConfigChangeState;
import bots.mctsbot.client.common.gamestate.modifiers.FoldState;
import bots.mctsbot.client.common.gamestate.modifiers.JoinTableState;
import bots.mctsbot.client.common.gamestate.modifiers.LeaveTableState;
import bots.mctsbot.client.common.gamestate.modifiers.NewCommunityCardsState;
import bots.mctsbot.client.common.gamestate.modifiers.NewDealState;
import bots.mctsbot.client.common.gamestate.modifiers.NewPocketCardsState;
import bots.mctsbot.client.common.gamestate.modifiers.NewRoundState;
import bots.mctsbot.client.common.gamestate.modifiers.NextPlayerState;
import bots.mctsbot.client.common.gamestate.modifiers.RaiseState;
import bots.mctsbot.client.common.gamestate.modifiers.ShowHandState;
import bots.mctsbot.client.common.gamestate.modifiers.SitInState;
import bots.mctsbot.client.common.gamestate.modifiers.SitOutState;
import bots.mctsbot.client.common.gamestate.modifiers.WinnerState;
import bots.mctsbot.client.common.playerstate.PlayerState;
import bots.mctsbot.common.elements.table.Round;

public class PlayerTrackingVisitor implements GameStateVisitor, Cloneable {

	private final static Logger logger = Logger.getLogger(PlayerTrackingVisitor.class);

	private GameState previousStartState;

	protected Propositionalizer propz = new Propositionalizer();

	public void readHistory(GameState gameState) {
		try {
			gameState.acceptHistoryVisitor(this, previousStartState);
		} catch (StackOverflowError e) {
			logger.error("Previous:" + previousStartState);
			logger.error("Current:" + gameState);
			throw e;
		}
		previousStartState = gameState;
	}

	@Override
	protected PlayerTrackingVisitor clone() {
		try {
			PlayerTrackingVisitor clone = (PlayerTrackingVisitor) super.clone();
			clone.setPropz(clone.getPropz().clone());
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void setPropz(Propositionalizer propz) {
		this.propz = propz;
	}

	public Propositionalizer getPropz() {
		return propz;
	}

	@Override
	public void visitAllInState(AllInState allInState) {
		propz.signalAllIn(allInState.getEvent().getPlayerId(), allInState.getEvent().getMovedAmount());
	}

	@Override
	public void visitBetState(BetState betState) {
		propz.signalBet(false, betState.getEvent().getPlayerId(), betState.getEvent().getAmount());
	}

	@Override
	public void visitCallState(CallState callState) {
		propz.signalCall(false, callState.getEvent().getPlayerId());
	}

	@Override
	public void visitCheckState(CheckState checkState) {
		propz.signalCheck(checkState.getEvent().getPlayerId());
	}

	@Override
	public void visitFoldState(FoldState foldState) {
		propz.signalFold(foldState.getEvent().getPlayerId());
	}

	@Override
	public void visitInitialGameState(DetailedHoldemTableState initialGameState) {

	}

	@Override
	public void visitJoinTableState(JoinTableState joinTableState) {

	}

	@Override
	public void visitLeaveTableState(LeaveTableState leaveTableState) {

	}

	@Override
	public void visitNewCommunityCardsState(NewCommunityCardsState newCommunityCardsState) {

	}

	@Override
	public void visitNewDealState(NewDealState newDealState) {
		propz.signalBBAmount(newDealState.getTableConfiguration().getBigBlind());
		propz.signalNewGame();
		for (PlayerState player : newDealState.getAllSeatedPlayers()) {
			propz.signalSeatedPlayer(player.getStack(), player.getPlayerId());
		}
	}

	@Override
	public void visitNewPocketCardsState(NewPocketCardsState newPocketCardsState) {

	}

	@Override
	public void visitNewRoundState(NewRoundState newRoundState) {
		if (newRoundState.getRound() == Round.FLOP) {
			propz.signalFlop();
		} else if (newRoundState.getRound() == Round.TURN) {
			propz.signalTurn();
		} else if (newRoundState.getRound() == Round.FINAL) {
			propz.signalRiver();
		}
	}

	@Override
	public void visitNextPlayerState(NextPlayerState nextPlayerState) {

	}

	@Override
	public void visitRaiseState(RaiseState raiseState) {
		propz.signalRaise(false, raiseState.getLastEvent().getPlayerId(), raiseState.getLargestBet());
	}

	@Override
	public void visitShowHandState(ShowHandState showHandState) {
	}

	@Override
	public void visitSitInState(SitInState sitInState) {

	}

	@Override
	public void visitSitOutState(SitOutState sitOutState) {

	}

	@Override
	public void visitBlindState(BlindState blindState) {
		propz.signalBlind(false, blindState.getLastEvent().getPlayerId(), blindState.getLastEvent().getAmount());
	}

	@Override
	public void visitWinnerState(WinnerState winnerState) {
	}

	@Override
	public void visitConfigChangeState(ConfigChangeState configChangeState) {
		propz.signalBBAmount(configChangeState.getLastEvent().getTableConfig().getBigBlind());
	}
}
