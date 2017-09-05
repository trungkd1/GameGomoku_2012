package com.bohemian.engine;

public enum CellStatus {
	empty(0),
	playerX(1),
	playerXWin(-1),
	playerO(2),
	playerOWin(-2);
	
	private final int id;

	CellStatus(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
