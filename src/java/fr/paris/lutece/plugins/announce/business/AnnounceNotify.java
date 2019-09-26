package fr.paris.lutece.plugins.announce.business;

public class AnnounceNotify
{

    private Integer _nId;
    private Integer _nIdAnnounce;

    public Integer getId( )
    {
        return _nId;
    }

    public void setId( Integer _nId )
    {
        this._nId = _nId;
    }

    public Integer getIdAnnounce( )
    {
        return _nIdAnnounce;
    }

    public void setIdAnnounce( Integer _nIdAnnounce )
    {
        this._nIdAnnounce = _nIdAnnounce;
    }
}
