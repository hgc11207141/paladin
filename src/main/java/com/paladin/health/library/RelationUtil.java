package com.paladin.health.library;

public class RelationUtil {

	public static boolean isSatisfied(Relation relation, String[] targetValue, String... sourceValue) {

		if (relation == Relation.EQUAL) {

			if (targetValue.length != sourceValue.length) {
				return false;
			}

			for (String tv : targetValue) {

				boolean b = false;

				for (String sv : sourceValue) {
					if (tv.equals(sv)) {
						b = true;
						break;
					}
				}

				if (!b) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.NOT_EQUAL) {

			for (String tv : targetValue) {
				for (String sv : sourceValue) {
					if (tv.equals(sv)) {
						return false;
					}
				}
			}
			return true;
		} else if (relation == Relation.IN) {

			if (targetValue.length > sourceValue.length) {
				return false;
			}

			for (String tv : targetValue) {

				boolean b = false;

				for (String sv : sourceValue) {
					if (tv.equals(sv)) {
						b = true;
						break;
					}
				}

				if (!b) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.HAVE) {

			if (targetValue.length < sourceValue.length) {
				return false;
			}

			for (String sv : sourceValue) {

				boolean b = false;

				for (String tv : targetValue) {
					if (tv.equals(sv)) {
						b = true;
						break;
					}
				}

				if (!b) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.BETWEEN) {
			double min = Double.valueOf(sourceValue[0]);
			double max = Double.valueOf(sourceValue[1]);

			for (String tv : targetValue) {
				double x = Double.valueOf(tv);
				if (x <= min || x >= max) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.BETWEEN_EQUAL) {

			double min = Double.valueOf(sourceValue[0]);
			double max = Double.valueOf(sourceValue[1]);

			for (String tv : targetValue) {
				double x = Double.valueOf(tv);
				if (x < min || x > max) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.GREAT) {

			double y = Double.valueOf(sourceValue[0]);

			for (String tv : targetValue) {
				double x = Double.valueOf(tv);
				if (x <= y) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.GREAT_EQUAL) {

			double y = Double.valueOf(sourceValue[0]);

			for (String tv : targetValue) {
				double x = Double.valueOf(tv);
				if (x < y) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.LESS) {

			double y = Double.valueOf(sourceValue[0]);

			for (String tv : targetValue) {
				double x = Double.valueOf(tv);
				if (x >= y) {
					return false;
				}
			}
			return true;
		} else if (relation == Relation.LESS_EQUAL) {

			double y = Double.valueOf(sourceValue[0]);

			for (String tv : targetValue) {
				double x = Double.valueOf(tv);
				if (x > y) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public static boolean isSatisfied(Relation relation, String targetValue, String... sourceValue) {

		if (relation == Relation.EQUAL) {
			return targetValue.equals(sourceValue[0]);
		} else if (relation == Relation.NOT_EQUAL) {
			return !targetValue.equals(sourceValue[0]);
		} else if (relation == Relation.IN) {
			for (String sv : sourceValue) {
				if (targetValue.equals(sv)) {
					return true;
				}
			}
			return false;
		} else if (relation == Relation.HAVE) {	
			return targetValue.equals(sourceValue[0]);			
		} else if (relation == Relation.BETWEEN) {

			double tv = Double.valueOf(targetValue);
			double min = Double.valueOf(sourceValue[0]);
			double max = Double.valueOf(sourceValue[1]);

			return tv > min && tv < max;

		} else if (relation == Relation.BETWEEN_EQUAL) {

			double tv = Double.valueOf(targetValue);
			double min = Double.valueOf(sourceValue[0]);
			double max = Double.valueOf(sourceValue[1]);

			return tv >= min && tv <= max;
		} else if (relation == Relation.GREAT) {

			double tv = Double.valueOf(targetValue);
			double sv = Double.valueOf(sourceValue[0]);

			return tv > sv;
		} else if (relation == Relation.GREAT_EQUAL) {

			double tv = Double.valueOf(targetValue);
			double sv = Double.valueOf(sourceValue[0]);

			return tv >= sv;
		} else if (relation == Relation.LESS) {

			double tv = Double.valueOf(targetValue);
			double sv = Double.valueOf(sourceValue[0]);

			return tv < sv;
		} else if (relation == Relation.LESS_EQUAL) {

			double tv = Double.valueOf(targetValue);
			double sv = Double.valueOf(sourceValue[0]);

			return tv <= sv;
		}

		return false;
	}

}
