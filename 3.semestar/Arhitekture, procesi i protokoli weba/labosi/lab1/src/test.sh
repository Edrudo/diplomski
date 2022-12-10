curl -X POST https://localhost:8000/gameweeks \
   -H 'Content-Type: application/json' \
   -d '{"gameweek":[{
				"team1": "VPS",
				"team2": "AC Oulu",
				"scoreTeam1": "1",
				"scoreTeam2": "2",
				"matchNumber": "1"
			}, {
				"team1": "SJK",
				"team2": "FC Haka",
				"scoreTeam1": "3",
				"scoreTeam2": "4",
				"matchNumber": "2"
			}, {
				"team1": "KuPS",
				"team2": "FC Honka",
				"scoreTeam1": "2",
				"scoreTeam2": "4",
				"matchNumber": "3"
			}, {
				"team1": "Ilves",
				"team2": "FC Inter",
				"scoreTeam1": "2",
				"scoreTeam2": "5",
				"matchNumber": "4"
			}, {
				"team1": "IFK Mariehamn",
				"team2": "FC Lahti",
				"scoreTeam1": "2",
				"scoreTeam2": "5",
				"matchNumber": "5"
			}, {
				"team1": "HJK",
				"team2": "HIFK",
				"scoreTeam1": "1",
				"scoreTeam2": "5",
				"matchNumber": "6"
			}]}'