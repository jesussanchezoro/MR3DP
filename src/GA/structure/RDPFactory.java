package GA.structure;

import grafo.optilib.structure.InstanceFactory;

public class RDPFactory extends InstanceFactory<RDPInstance> {
    @Override
    public RDPInstance readInstance(String s) {
        return new RDPInstance(s);
    }
}
