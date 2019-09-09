// Liam Phelan 17451926
// Hugh McKeeney 17324636
// Hannah O'Dea 17405444

public class DoublingCube
{
    private int value;
    private int owner;

    DoublingCube()
    {
        value = 1;
        owner = -1;
    }

    public int doubleCube(int passTo)
    {
        owner = passTo;
        value *= 2;
        return value;
    }

    public int getOwner(){ return owner; }

    public void setOwner(int passTo){ owner = passTo; }

    public int getDoubleValue(){ return value; }

    public void setDoubleValue( int doubleValue ) { value = doubleValue; }
}
