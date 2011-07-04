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
package bots.mctsbot.client.common.gamestate;

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

public interface GameStateVisitor {

	/** unneeded */
	void visitInitialGameState(DetailedHoldemTableState initialGameState);

	void visitAllInState(AllInState allInState);

	void visitBetState(BetState betState);

	void visitCallState(CallState callState);

	void visitCheckState(CheckState checkState);

	void visitFoldState(FoldState foldState);

	void visitJoinTableState(JoinTableState joinTableState);

	void visitLeaveTableState(LeaveTableState leaveTableState);

	void visitNewCommunityCardsState(NewCommunityCardsState newCommunityCardsState);

	void visitNewDealState(NewDealState newDealState);

	void visitNewPocketCardsState(NewPocketCardsState newPocketCardsState);

	void visitNewRoundState(NewRoundState newRoundState);

	void visitNextPlayerState(NextPlayerState nextPlayerState);

	void visitRaiseState(RaiseState raiseState);

	void visitShowHandState(ShowHandState showHandState);

	void visitSitInState(SitInState sitInState);

	void visitSitOutState(SitOutState sitOutState);

	void visitWinnerState(WinnerState winnerState);

	void visitConfigChangeState(ConfigChangeState configChangeState);

	void visitBlindState(BlindState blindState);

}
