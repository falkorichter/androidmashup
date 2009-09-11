<?php

/**
 * autoIntentHandlesExtra actions.
 *
 * @package    ##PROJECT_NAME##
 * @subpackage autoIntentHandlesExtra
 * @author     ##AUTHOR_NAME##
 * @version    SVN: $Id: actions.class.php 16948 2009-04-03 15:52:30Z fabien $
 */
class autoIntentHandlesExtraActions extends sfActions
{
  public function executeIndex($request)
  {
    return $this->forward('IntentHandlesExtra', 'list');
  }

  public function executeList($request)
  {
    $this->processSort();

    $this->processFilters();


    // pager
    $this->pager = new sfPropelPager('IntentHandlesExtra', 20);
    $c = new Criteria();
    $this->addSortCriteria($c);
    $this->addFiltersCriteria($c);
    $this->pager->setCriteria($c);
    $this->pager->setPage($this->getRequestParameter('page', $this->getUser()->getAttribute('page', 1, 'sf_admin/intent_handles_extra')));
    $this->pager->init();
    // save page
    if ($this->getRequestParameter('page')) {
        $this->getUser()->setAttribute('page', $this->getRequestParameter('page'), 'sf_admin/intent_handles_extra');
    }
  }

  public function executeCreate($request)
  {
    return $this->forward('IntentHandlesExtra', 'edit');
  }

  public function executeSave($request)
  {
    return $this->forward('IntentHandlesExtra', 'edit');
  }


  public function executeDeleteSelected($request)
  {
    $this->selectedItems = $this->getRequestParameter('sf_admin_batch_selection', array());

    try
    {
      foreach (IntentHandlesExtraPeer::retrieveByPks($this->selectedItems) as $object)
      {
        $object->delete();
      }
    }
    catch (PropelException $e)
    {
      $request->setError('delete', 'Could not delete the selected Intent handles extras. Make sure they do not have any associated items.');
      return $this->forward('IntentHandlesExtra', 'list');
    }

    return $this->redirect('IntentHandlesExtra/list');
  }

  public function executeEdit($request)
  {
    $this->intent_handles_extra = $this->getIntentHandlesExtraOrCreate();

    if ($request->isMethod('post'))
    {
      $this->updateIntentHandlesExtraFromRequest();

      try
      {
        $this->saveIntentHandlesExtra($this->intent_handles_extra);
      }
      catch (PropelException $e)
      {
        $request->setError('edit', 'Could not save the edited Intent handles extras.');
        return $this->forward('IntentHandlesExtra', 'list');
      }

      $this->getUser()->setFlash('notice', 'Your modifications have been saved');

      if ($this->getRequestParameter('save_and_add'))
      {
        return $this->redirect('IntentHandlesExtra/create');
      }
      else if ($this->getRequestParameter('save_and_list'))
      {
        return $this->redirect('IntentHandlesExtra/list');
      }
      else
      {
        return $this->redirect('IntentHandlesExtra/edit?id_intent_handles_extra='.$this->intent_handles_extra->getIdIntentHandlesExtra());
      }
    }
    else
    {
      $this->labels = $this->getLabels();
    }
  }

  public function executeDelete($request)
  {
    $this->intent_handles_extra = IntentHandlesExtraPeer::retrieveByPk($this->getRequestParameter('id_intent_handles_extra'));
    $this->forward404Unless($this->intent_handles_extra);

    try
    {
      $this->deleteIntentHandlesExtra($this->intent_handles_extra);
    }
    catch (PropelException $e)
    {
      $request->setError('delete', 'Could not delete the selected Intent handles extra. Make sure it does not have any associated items.');
      return $this->forward('IntentHandlesExtra', 'list');
    }

    return $this->redirect('IntentHandlesExtra/list');
  }

  public function handleErrorEdit()
  {
    $this->preExecute();
    $this->intent_handles_extra = $this->getIntentHandlesExtraOrCreate();
    $this->updateIntentHandlesExtraFromRequest();

    $this->labels = $this->getLabels();

    return sfView::SUCCESS;
  }

  protected function saveIntentHandlesExtra($intent_handles_extra)
  {
    $intent_handles_extra->save();

  }

  protected function deleteIntentHandlesExtra($intent_handles_extra)
  {
    $intent_handles_extra->delete();
  }

  protected function updateIntentHandlesExtraFromRequest()
  {
    $intent_handles_extra = $this->getRequestParameter('intent_handles_extra');

    if (isset($intent_handles_extra['intent_id']))
    {
    $this->intent_handles_extra->setIntentId($intent_handles_extra['intent_id'] ? $intent_handles_extra['intent_id'] : null);
    }
    if (isset($intent_handles_extra['extra_id']))
    {
    $this->intent_handles_extra->setExtraId($intent_handles_extra['extra_id'] ? $intent_handles_extra['extra_id'] : null);
    }
    if (isset($intent_handles_extra['mandatory']))
    {
      $this->intent_handles_extra->setMandatory($intent_handles_extra['mandatory']);
    }
    if (isset($intent_handles_extra['description']))
    {
      $this->intent_handles_extra->setDescription($intent_handles_extra['description']);
    }
  }

  protected function getIntentHandlesExtraOrCreate($id_intent_handles_extra = 'id_intent_handles_extra')
  {
    if ($this->getRequestParameter($id_intent_handles_extra) === ''
     || $this->getRequestParameter($id_intent_handles_extra) === null)
    {
      $intent_handles_extra = new IntentHandlesExtra();
    }
    else
    {
      $intent_handles_extra = IntentHandlesExtraPeer::retrieveByPk($this->getRequestParameter($id_intent_handles_extra));

      $this->forward404Unless($intent_handles_extra);
    }

    return $intent_handles_extra;
  }

  protected function processFilters()
  {
  }

  protected function processSort()
  {
    if ($this->getRequestParameter('sort'))
    {
      $this->getUser()->setAttribute('sort', $this->getRequestParameter('sort'), 'sf_admin/intent_handles_extra/sort');
      $this->getUser()->setAttribute('type', $this->getRequestParameter('type', 'asc'), 'sf_admin/intent_handles_extra/sort');
    }

    if (!$this->getUser()->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort'))
    {
    }
  }

  protected function addFiltersCriteria($c)
  {
  }

  protected function addSortCriteria($c)
  {
    if ($sort_column = $this->getUser()->getAttribute('sort', null, 'sf_admin/intent_handles_extra/sort'))
    {
      // camelize lower case to be able to compare with BasePeer::TYPE_PHPNAME translate field name
      $sort_column = IntentHandlesExtraPeer::translateFieldName(sfInflector::camelize(strtolower($sort_column)), BasePeer::TYPE_PHPNAME, BasePeer::TYPE_COLNAME);
      if ($this->getUser()->getAttribute('type', null, 'sf_admin/intent_handles_extra/sort') == 'asc')
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
      'intent_handles_extra{id_intent_handles_extra}' => 'Id intent handles extra:',
      'intent_handles_extra{intent_id}' => 'Intent:',
      'intent_handles_extra{extra_id}' => 'Extra:',
      'intent_handles_extra{mandatory}' => 'Mandatory:',
      'intent_handles_extra{description}' => 'Description:',
    );
  }
}
