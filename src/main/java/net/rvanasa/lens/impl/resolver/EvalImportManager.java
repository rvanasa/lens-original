package net.rvanasa.lens.impl.resolver;

import net.rvanasa.lens.LensImportManager;
import net.rvanasa.lens.LensImporter;

public class EvalImportManager implements LensImportManager
{
	private final ResourceDiscoverer discoverer;
	
	public EvalImportManager(ResourceDiscoverer discoverer)
	{
		this.discoverer = discoverer;
	}
	
	public ResourceDiscoverer getDiscoverer()
	{
		return discoverer;
	}
	
	@Override
	public LensImporter createImporter(String path)
	{
		return new LensEvalImporter(getDiscoverer().getResource(path));
	}
	
	public interface ResourceDiscoverer
	{
		public String getResource(String path);
	}
}
