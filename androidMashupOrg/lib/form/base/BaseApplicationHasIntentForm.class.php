<?php

/**
 * ApplicationHasIntent form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseApplicationHasIntentForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_application_has_intent' => new sfWidgetFormInputHidden(),
      'application_id'            => new sfWidgetFormPropelChoice(array('model' => 'Application', 'add_empty' => false)),
      'intent_id'                 => new sfWidgetFormPropelChoice(array('model' => 'Intent', 'add_empty' => false)),
    ));

    $this->setValidators(array(
      'id_application_has_intent' => new sfValidatorPropelChoice(array('model' => 'ApplicationHasIntent', 'column' => 'id_application_has_intent', 'required' => false)),
      'application_id'            => new sfValidatorPropelChoice(array('model' => 'Application', 'column' => 'id_application')),
      'intent_id'                 => new sfValidatorPropelChoice(array('model' => 'Intent', 'column' => 'id_intent')),
    ));

    $this->widgetSchema->setNameFormat('application_has_intent[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'ApplicationHasIntent';
  }


}
