package es.uvigo.esei.daa.dataset;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;

import java.util.Arrays;
import java.util.function.Predicate;

import es.uvigo.esei.daa.entities.Person;
import es.uvigo.esei.daa.entities.Pet;

public final class PetsDataset {
	private PetsDataset() {}
	
	public static Pet[] pets() {
		return new Pet[] {
			new Pet(1, "Puppy", new Person(1,"Antón", "Álvarez")),
			new Pet(2, "Perriyo", new Person(2, "Ana", "Amargo")),
			new Pet(3, "Gatiyo", new Person(3, "Manuel", "Martínez")),
			new Pet(4, "Pececiyo", new Person(4, "María", "Márquez")),
			new Pet(5, "Tom", new Person(5, "Lorenzo", "López")),
			new Pet(6, "Jerry", new Person(6, "Laura", "Laredo")),
		};
	}
	
	public static Pet[] petsWithout(int ... ids) {
		Arrays.sort(ids);
		
		final Predicate<Pet> hasValidId = pet ->
			binarySearch(ids, pet.getId()) < 0;
		
		return stream(pets()) // ojo que tendra que llevar  int ownerID seguramente
			.filter(hasValidId)
		.toArray(Pet[]::new);
	}
	
	public static Pet pet(int id) {
		return stream(pets())
			.filter(pet -> pet.getId() == id)
			.findAny()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static int existentId() {
		return 5;
	}
	
	public static int nonExistentId() {
		return 1234;
	}

	public static Pet existentPet() {
		return pet(existentId());
	}
	
	public static Pet nonExistentPet() {
		return new Pet(nonExistentId(), "Tom", new Person(1,"Antón", "Álvarez"));
	}
	
	public static String newName() {
		return "John";
	}
	
	public static Person newOwner() {
		return new Person(7, "Perico", "Palotes");
	}
	
	
	public static Pet newPet() {
		return new Pet(pets().length + 1, newName(), newOwner());
	}

}
