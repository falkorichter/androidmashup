<?php

require_once(sfConfig::get('sf_lib_dir').'/filter/base/BaseFormFilterPropel.class.php');

/**
 * IntentHandlesExtra filter form base class.
 *
 * @package    mashup
 * @subpackage filter
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormFilterGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseIntentHandlesExtraFormFilter extends BaseFormFilterPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'intent_id'               => new sfWidgetFormPropelChoice(array('model' => 'Intent', 'add_empty' => true)),
      'extra_id'                => new sfWidgetFormPropelChoice(array('model' => 'Extra', 'add_empty' => true)),
      'mandatory'               => new sfWidgetFormFilterInput(),
    ));

    $this->setValidators(array(
      'intent_id'               => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Intent', 'column' => 'id_intent')),
      'extra_id'                => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Extra', 'column' => 'id_extra')),
      'mandatory'               => new sfValidatorPass(array('required' => false)),
    ));

    $this->widgetSchema->setNameFormat('intent_handles_extra_filters[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'IntentHandlesExtra';
  }

  public function getFields()
  {
    return array(
      'id_intent_handles_extra' => 'Number',
      'intent_id'               => 'ForeignKey',
      'extra_id'                => 'ForeignKey',
      'mandatory'               => 'Text',
    );
  }
}
