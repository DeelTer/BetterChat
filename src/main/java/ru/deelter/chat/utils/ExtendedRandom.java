package ru.deelter.chat.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExtendedRandom extends Random {

	private static final ExtendedRandom instance = new ExtendedRandom();

	public ExtendedRandom() {
	}

	public ExtendedRandom(long seed) {
		super(seed);
	}

	@NotNull
	public static ExtendedRandom getInstance() {
		return instance;
	}

	public int getInt(int min, int max) {
		return this.nextInt(1 + max - min) + min;
	}

	public double getDouble(double min, double max) {
		return min + (max - min) * nextDouble();
	}

	public long getLong(long min, long max) {
		return this.nextLong(1 + max - min) + min;
	}

	public float getFloat(float min, float max) {
		return this.nextFloat() * (max - min);
	}

	public int getInt(int bound) {
		return this.nextInt(bound);
	}

	public double getDouble() {
		return this.nextDouble();
	}

	public boolean getBoolean() {
		return this.nextBoolean();
	}

	public boolean rollDouble(double d) {
		return nextDouble() <= d;
	}

	@NotNull
	public String getRandomString(int length) {
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char random = (char) (this.nextInt(26) + 'a');
			line.append(random);
		}
		return line.toString();
	}

	@Nullable
	public <T> T getRandom(@NotNull List<T> list) {
		if (list.isEmpty())
			return null;
		if (list.size() == 1)
			return list.get(0);
		return list.get(this.getInt(list.size()));
	}

	@NotNull
	public <T> T getRandom(@NotNull List<T> list, @NotNull T defaultValue) {
		T value = this.getRandom(list);
		return value == null ? defaultValue : value;
	}

	@NotNull
	public <T> List<T> getRandom(List<T> list, int count) {
		List<T> finalList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			T randomValue = this.getRandom(list);
			if (randomValue == null)
				continue;
			finalList.add(randomValue);
		}
		return finalList;
	}

	@NotNull
	public <T> T getRandom(@NotNull T[] array) {
		return array[this.getInt(array.length)];
	}

}