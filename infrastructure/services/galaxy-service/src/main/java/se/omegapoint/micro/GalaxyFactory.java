package se.omegapoint.micro;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class GalaxyFactory {

    public Galaxy randomGalaxy() {
        return new Galaxy(galaxies.get(new Random().nextInt(galaxies.size() - 1)));
    }

    List<String> galaxies = Arrays.asList("Abell 1835 IR1916",
            "Abell 2142",
            "Andromeda galaxy (M31)",
            "Andromeda I",
            "Andromeda II",
            "Andromeda III",
            "Andromeda XIX",
            "Baby boom galaxy",
            "Barnards galaxy (NGC 6822)",
            "Black Eye galaxy (M64)",
            "Bode's galaxy (M81)",
            "Canis Major dwarf galaxy",
            "Cartwheel galaxy",
            "Centaurus A galaxy (NGC 5128)",
            "Circinus galaxy",
            "Cigar galaxy (M82)",
            "GN-z11 (Farthest-known and oldest-known galaxy)",
            "Hoag's object (a ring galaxy)",
            "IC 10",
            "IC 1101 largest known galaxy",
            "IC 1613",
            "Large Magellanic Cloud",
            "Leo I",
            "Leo II",
            "LGS 3",
            "Messier 49 (NGC 4472)",
            "Messier 83 Southern Pinwheel galaxy",
            "Messier 84 (NGC 4374)",
            "Messier 87 (NGC 4486)",
            "Messier 100 (NGC 4321)",
            "Milky Way – home galaxy of the Solar system",
            "Magellanic Clouds",
            "NGC185",
            "NGC147",
            "NGC 205 (M110)",
            "NGS 221 (M32)",
            "NGC 4526",
            "NGC 6822",
            "Pinwheel galaxy (M101)",
            "Small Magellanic Cloud",
            "Sombrero galaxy (M104)",
            "Spindle galaxy (M102)",
            "Starfish galaxy",
            "Sunflower galaxy (M63)",
            "Triangulum galaxy (M33)",
            "UDFy-38135539 (HUDF.YD3) Furthest known object ever seen",
            "Whirlpool galaxy (M51)",
            "Wolf-Lundmark-Melotte (WLM)");

}
