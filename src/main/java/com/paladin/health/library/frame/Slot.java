package com.paladin.health.library.frame;

import com.paladin.health.library.Condition;

public interface Slot {

	public SlotMatchResult match(Condition condition);

}
