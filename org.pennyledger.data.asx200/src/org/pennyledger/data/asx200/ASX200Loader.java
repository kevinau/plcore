package org.pennyledger.data.asx200;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.dao.ITransaction;
import org.plcore.inbox.IFileProcessor;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.ReportableError;

import au.com.bytecode.opencsv.CSVReader;


@Component(name = "ASX200Loader")
public class ASX200Loader implements IFileProcessor {

  @Reference
  private IModelFactory modelFactory;
  
  @Reference(target = "(name=ASXCompany)")
  private IDataAccessObject<ASXCompany> daoCompany;

  @Reference(target = "(name=ASXSector)")
  private IDataAccessObject<ASXSector> daoSector;

  private void loadSectors (Path path) {
    List<String> sectors = new ArrayList<>();
    try (CSVReader reader = new CSVReader(new FileReader(path.toFile())))
    {
      // First heading line
      String [] line = reader.readNext();
      // Second heading line
      line = reader.readNext();
      // First data line
      line = reader.readNext();
      while (line != null) {
        if (!sectors.contains(line[2])) {
          sectors.add(line[2]);
        }
        line = reader.readNext();
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    
    IEntityModel entityModel = modelFactory.buildEntityModel(ASXSector.class);

    Collections.sort(sectors);
    int loaded = 0;
    
    try (ITransaction<ASXSector> tran = daoSector.getTransaction()) {
      for (String sectorName : sectors) {
        entityModel.setNew();
        IItemModel name = entityModel.selectItemModel("name");

        name.setValueFromSource(sectorName);
        List<ReportableError> errors = entityModel.getErrors();
        if (errors.size() == 0) {
          ASXSector sector = new ASXSector(sectorName);
          tran.add(sector);
          loaded++;
        } else {
          System.out.println(sectorName);
          for (ReportableError ex : errors) {
            System.out.println("  " + ex);
          }
        }
      }
      System.out.println(loaded + " ASX sectors loaded");
    }  
  }

  
  private void emptySectors () {
  }

  
  private void loadCompanies (Path path) {
    IEntityModel entityModel = modelFactory.buildEntityModel(ASXCompany.class);
    entityModel.setNew();
    IItemModel code = entityModel.selectItemModel("code");
    IItemModel company = entityModel.selectItemModel("company");
    IItemModel sector = entityModel.selectItemModel("sector");
    IItemModel marketCap = entityModel.selectItemModel("marketCap");
    IItemModel weight = entityModel.selectItemModel("weight");
    
    try (CSVReader reader = new CSVReader(new FileReader(path.toFile())))
    {
      try (ITransaction<ASXCompany> tran = daoCompany.getTransaction()) {
        // First heading line
        String [] line = reader.readNext();
        // Second heading line
        line = reader.readNext();
        int loaded = 0;

        // First data line
        line = reader.readNext();
        while (line != null) {
          entityModel.syncValue(new ASXCompany(), true);
          
          code.setValueFromSource(line[0]);
          company.setValueFromSource(line[1]);
          sector.setValueFromSource(line[2]);
          marketCap.setValueFromSource(line[3].replaceAll(",", ""));
          weight.setValueFromSource(line[4]);
          List<ReportableError> errors = entityModel.getErrors();
          if (errors.size() == 0) {
            ASXCompany asx200 = entityModel.getValue();
            tran.add(asx200);
            loaded++;
          } else {
            System.out.println(Arrays.toString(line));
            for (ReportableError ex : errors) {
              System.out.println(ex);
            }
          }
          line = reader.readNext();
        }
        System.out.println(loaded + " ASX companies loaded");
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  private void emptyCompanies () {
  }

  
//  @Activate
//  public void activate(BundleContext context) {
//    URL url = context.getBundle().getResource("data/20180401-asx200.csv");
//    loadSectors(url);
//    loadCompanies(url);
//  }


  @Override
  public void process(Path path, String digest) {
    loadSectors(path);
    loadCompanies(path);
  }

  @Override
  public void unprocess(Path path, String digest) {
    emptyCompanies();
    emptySectors();
  }
}
