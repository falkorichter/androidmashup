<?php

/**
 * autoExtratype actions.
 *
 * @package    ##PROJECT_NAME##
 * @subpackage autoExtratype
 * @author     ##AUTHOR_NAME##
 * @version    SVN: $Id: actions.class.php 16948 2009-04-03 15:52:30Z fabien $
 */
class autoExtratypeActions extends sfActions
{
  public function executeIndex($request)
  {
    return $this->forward('extratype', 'list');
  }

  public function executeList($request)
  {
    $this->processSort();

    $this->processFilters();


    // pager
    $this->pager = new sfPropelPager('Extratype', 20);
    $c = new Criteria();
    $this->addSortCriteria($c);
    $this->addFiltersCriteria($c);
    $this->pager->setCriteria($c);
    $this->pager->setPage($this->getRequestParameter('page', $this->getUser()->getAttribute('page', 1, 'sf_admin/extratype')));
    $this->pager->init();
    // save page
    if ($this->getRequestParameter('page')) {
        $this->getUser()->setAttribute('page', $this->getRequestParameter('page'), 'sf_admin/extratype');
    }
  }

  public function executeCreate($request)
  {
    return $this->forward('extratype', 'edit');
  }

  public function executeSave($request)
  {
    return $this->forward('extratype', 'edit');
  }


  public function executeDeleteSelected($request)
  {
    $this->selectedItems = $this->getRequestParameter('sf_admin_batch_selection', array());

    try
    {
      foreach (ExtratypePeer::retrieveByPks($this->selectedItems) as $object)
      {
        $object->delete();
      }
    }
    catch (PropelException $e)
    {
      $request->setError('delete', 'Could not delete the selected Extratypes. Make sure they do not have any associated items.');
      return $this->forward('extratype', 'list');
    }

    return $this->redirect('extratype/list');
  }

  public function executeEdit($request)
  {
    $this->extratype = $this->getExtratypeOrCreate();

    if ($request->isMethod('post'))
    {
      $this->updateExtratypeFromRequest();

      try
      {
        $this->saveExtratype($this->extratype);
      }
      catch (PropelException $e)
      {
        $request->setError('edit', 'Could not save the edited Extratypes.');
        return $this->forward('extratype', 'list');
      }

      $this->getUser()->setFlash('notice', 'Your modifications have been saved');

      if ($this->getRequestParameter('save_and_add'))
      {
        return $this->redirect('extratype/create');
      }
      else if ($this->getRequestParameter('save_and_list'))
      {
        return $this->redirect('extratype/list');
      }
      else
      {
        return $this->redirect('extratype/edit?id_extratype='.$this->extratype->getIdExtratype());
      }
    }
    else
    {
      $this->labels = $this->getLabels();
    }
  }

  public function executeDelete($request)
  {
    $this->extratype = ExtratypePeer::retrieveByPk($this->getRequestParameter('id_extratype'));
    $this->forward404Unless($this->extratype);

    try
    {
      $this->deleteExtratype($this->extratype);
    }
    catch (PropelException $e)
    {
      $request->setError('delete', 'Could not delete the selected Extratype. Make sure it does not have any associated items.');
      return $this->forward('extratype', 'list');
    }

    return $this->redirect('extratype/list');
  }

  public function handleErrorEdit()
  {
    $this->preExecute();
    $this->extratype = $this->getExtratypeOrCreate();
    $this->updateExtratypeFromRequest();

    $this->labels = $this->getLabels();

    return sfView::SUCCESS;
  }

  protected function saveExtratype($extratype)
  {
    $extratype->save();

  }

  protected function deleteExtratype($extratype)
  {
    $extratype->delete();
  }

  protected function updateExtratypeFromRequest()
  {
    $extratype = $this->getRequestParameter('extratype');

    if (isset($extratype['name']))
    {
      $this->extratype->setName($extratype['name']);
    }
    if (isset($extratype['description']))
    {
      $this->extratype->setDescription($extratype['description']);
    }
    if (isset($extratype['java_retrieve_command']))
    {
      $this->extratype->setJavaRetrieveCommand($extratype['java_retrieve_command']);
    }
  }

  protected function getExtratypeOrCreate($id_extratype = 'id_extratype')
  {
    if ($this->getRequestParameter($id_extratype) === ''
     || $this->getRequestParameter($id_extratype) === null)
    {
      $extratype = new Extratype();
    }
    else
    {
      $extratype = ExtratypePeer::retrieveByPk($this->getRequestParameter($id_extratype));

      $this->forward404Unless($extratype);
    }

    return $extratype;
  }

  protected function processFilters()
  {
  }

  protected function processSort()
  {
    if ($this->getRequestParameter('sort'))
    {
      $this->getUser()->setAttribute('sort', $this->getRequestParameter('sort'), 'sf_admin/extratype/sort');
      $this->getUser()->setAttribute('type', $this->getRequestParameter('type', 'asc'), 'sf_admin/extratype/sort');
    }

    if (!$this->getUser()->getAttribute('sort', null, 'sf_admin/extratype/sort'))
    {
    }
  }

  protected function addFiltersCriteria($c)
  {
  }

  protected function addSortCriteria($c)
  {
    if ($sort_column = $this->getUser()->getAttribute('sort', null, 'sf_admin/extratype/sort'))
    {
      // camelize lower case to be able to compare with BasePeer::TYPE_PHPNAME translate field name
      $sort_column = ExtratypePeer::translateFieldName(sfInflector::camelize(strtolower($sort_column)), BasePeer::TYPE_PHPNAME, BasePeer::TYPE_COLNAME);
      if ($this->getUser()->getAttribute('type', null, 'sf_admin/extratype/sort') == 'asc')
      {
        $c->addAscendingOrderByColumn($sort_column);
      }
      else
      {
        $c->addDescendingOrderByColumn($sort_column);
      }
    }
  }

  protected function getLabels()
  {
    return array(
      'extratype{id_extratype}' => 'Id extratype:',
      'extratype{name}' => 'Name:',
      'extratype{description}' => 'Description:',
      'extratype{java_retrieve_command}' => 'Java retrieve command:',
    );
  }
}
